package beworkify.service.impl;

import beworkify.dto.request.PostRequest;
import beworkify.dto.response.EmployerSummaryResponse;
import beworkify.dto.response.PageResponse;
import beworkify.dto.response.PostResponse;
import beworkify.dto.response.UserSummaryResponse;
import beworkify.entity.CategoryPost;
import beworkify.entity.Employer;
import beworkify.entity.Post;
import beworkify.entity.User;
import beworkify.enumeration.StatusPost;
import beworkify.enumeration.UserRole;
import beworkify.exception.ResourceNotFoundException;
import beworkify.mapper.PostMapper;
import beworkify.repository.CategoryPostRepository;
import beworkify.repository.PostRepository;
import beworkify.service.AzureBlobService;
import beworkify.service.EmployerService;
import beworkify.service.PostService;
import beworkify.service.UserService;
import beworkify.util.AppUtils;
import beworkify.util.HtmlImageProcessor;
import beworkify.util.RedisUtils;
import beworkify.util.TagUtils;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

  private final PostRepository repository;
  private final PostMapper mapper;
  private final CategoryPostRepository categoryRepository;
  private final AzureBlobService storageService;
  private final UserService userService;
  private final EmployerService employerService;
  private final MessageSource messageSource;
  private final RedisUtils redisUtils;

  @Override
  @Transactional
  @CachePut(value = "posts", key = "#result.id", unless = "#result == null or #result.status != T(beworkify.enumeration.StatusPost).PUBLIC")
  public PostResponse create(PostRequest request, MultipartFile thumbnail) throws Exception {
    Post entity = mapper.toEntity(request);
    CategoryPost category = categoryRepository
        .findById(request.getCategoryId())
        .orElseThrow(
            () -> new ResourceNotFoundException(
                messageSource.getMessage(
                    "categoryPost.notFound", null, LocaleContextHolder.getLocale())));
    entity.setCategory(category);
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (AppUtils.hasRole(auth, UserRole.ADMIN.getName())) {
      Long id = AppUtils.getUserIdFromSecurityContext();
      User author = userService.findUserById(id);
      entity.setUserAuthor(author);
      entity.setStatus(StatusPost.fromValue(request.getStatus()));
    } else {
      Long id = AppUtils.getEmployerIdFromSecurityContext();
      Employer author = employerService.findEmployerById(id);
      entity.setEmployerAuthor(author);
      entity.setStatus(StatusPost.PENDING);
    }
    HtmlImageProcessor processor = new HtmlImageProcessor(storageService);
    String processedContent = processor.process(request.getContent());
    entity.setContent(processedContent);
    entity.setContentText(Jsoup.parse(processedContent).text());
    String tags = TagUtils.extractTagsAsPipe(processedContent);
    entity.setTags(tags);
    int words = AppUtils.countWords(processedContent);
    entity.setReadingTimeMinutes(Math.max(1, words / 200));
    entity.setSlug(AppUtils.toSlug(entity.getTitle()));
    if (thumbnail != null && !thumbnail.isEmpty()) {
      String thumbnailUrl = storageService.uploadFile(thumbnail);
      entity.setThumbnailUrl(thumbnailUrl);
    }
    repository.save(entity);
    PostResponse response = mapper.toDTO(entity);
    if (entity.getUserAuthor() != null) {
      response.setUserAuthor(
          UserSummaryResponse.builder()
              .id(entity.getUserAuthor().getId())
              .avatarUrl(entity.getUserAuthor().getAvatarUrl())
              .email(entity.getUserAuthor().getEmail())
              .fullName(entity.getUserAuthor().getFullName())
              .role(entity.getUserAuthor().getRole().getRole().getName())
              .build());
    } else if (entity.getEmployerAuthor() != null) {
      response.setEmployerAuthor(
          EmployerSummaryResponse.builder()
              .id(entity.getEmployerAuthor().getId())
              .email(entity.getEmployerAuthor().getEmail())
              .backgroundUrl(entity.getEmployerAuthor().getBackgroundUrl())
              .avatarUrl(entity.getEmployerAuthor().getAvatarUrl())
              .companyName(entity.getEmployerAuthor().getCompanyName())
              .createdAt(entity.getEmployerAuthor().getCreatedAt())
              .updatedAt(entity.getEmployerAuthor().getUpdatedAt())
              .employerSlug(entity.getEmployerAuthor().getEmployerSlug())
              .build());
    }
    evictCacheByPattern("posts:pn:*");
    evictCacheByPattern("posts:latest:*");
    evictCacheByPattern("posts:related:" + entity.getId() + ":*");
    return response;
  }

  @Override
  @Transactional
  @CachePut(value = "posts", key = "#result.id", unless = "#result == null or #result.status != T(beworkify.enumeration.StatusPost).PUBLIC")
  public PostResponse update(Long id, PostRequest request, MultipartFile thumbnail)
      throws Exception {
    Post entity = findPostById(id);
    validateOwner(entity);
    mapper.updateEntityFromRequest(request, entity);
    if (request.getCategoryId() != null) {
      CategoryPost category = categoryRepository
          .findById(request.getCategoryId())
          .orElseThrow(
              () -> new ResourceNotFoundException(
                  messageSource.getMessage(
                      "categoryPost.notFound", null, LocaleContextHolder.getLocale())));
      entity.setCategory(category);
    }

    if (request.getContent() != null) {
      HtmlImageProcessor processor = new HtmlImageProcessor(storageService);
      String processedContent = processor.process(request.getContent());
      entity.setContent(processedContent);
      entity.setContentText(Jsoup.parse(processedContent).text());
      entity.setTags(TagUtils.extractTagsAsPipe(processedContent));
      int words = AppUtils.countWords(processedContent);
      entity.setReadingTimeMinutes(Math.max(1, words / 200));
    }
    if (request.getTitle() != null) {
      entity.setSlug(AppUtils.toSlug(request.getTitle()));
    }
    if (thumbnail != null && !thumbnail.isEmpty()) {
      String thumbnailUrl = storageService.uploadFile(thumbnail);
      entity.setThumbnailUrl(thumbnailUrl);
    }

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (request.getStatus() != null && AppUtils.hasRole(auth, "ADMIN")) {
      entity.setStatus(StatusPost.fromValue(request.getStatus()));
    }

    repository.save(entity);
    PostResponse response = mapper.toDTO(entity);
    mapAuthorResponse(response, entity);
    evictCacheByPattern("posts:pn:*");
    evictCacheByPattern("posts:latest:*");
    evictCacheByPattern("posts:related:" + id + ":*");
    return response;
  }

  @Override
  @Transactional
  @CacheEvict(value = "posts", key = "#id")
  public void delete(Long id) {
    Post entity = findPostById(id);
    validateOwner(entity);
    repository.delete(entity);
    evictCacheByPattern("posts:pn:*");
    evictCacheByPattern("posts:latest:*");
    evictCacheByPattern("posts:related:" + id + ":*");
  }

  @Override
  public PostResponse getById(Long id) {
    Post entity = findPostById(id);
    validateOwnerAndPublic(entity);
    PostResponse response = mapper.toDTO(entity);
    mapAuthorResponse(response, entity);
    return response;
  }

  @Override
  @Cacheable(value = "posts", key = "@keyGenerator.buildKeyWithPaginationSortsKeywordForPost(#pageNumber, #pageSize, #sorts, #keyword, T(java.util.List).of('createdAt', 'updatedAt'),#categoryId, #isPublic, #authorId)")
  public PageResponse<List<PostResponse>> getAll(
      int pageNumber,
      int pageSize,
      List<String> sorts,
      String keyword,
      Long categoryId,
      boolean isPublic) {
    String kw = (keyword == null) ? "" : keyword.toLowerCase();
    List<String> whiteListFieldSorts = List.of("createdAt", "updatedAt");
    Pageable pageable = AppUtils.generatePageableWithSort(sorts, whiteListFieldSorts, pageNumber, pageSize);
    Page<Post> page;
    if (!isPublic) {
      page = repository.searchPosts(kw, categoryId, pageable);
    } else {
      page = repository.searchPublicPosts(kw, categoryId, StatusPost.PUBLIC, pageable);
    }
    List<PostResponse> items = page.getContent().stream()
        .map(
            entity -> {
              PostResponse response = mapper.toDTO(entity);
              mapAuthorResponse(response, entity);
              return response;
            })
        .toList();
    return PageResponse.<List<PostResponse>>builder()
        .pageNumber(pageNumber)
        .pageSize(pageSize)
        .totalPages(page.getTotalPages())
        .numberOfElements(page.getNumberOfElements())
        .items(items)
        .build();
  }

  @Override
  @Cacheable(value = "posts", key = "'related:' + #postId + ':' + #limit")
  public List<PostResponse> getRelated(Long postId, int limit) {
    Post anchor = findPostById(postId);

    List<Post> related = new ArrayList<>();
    Set<Long> addedIds = new HashSet<>();

    String searchText = cleanSearchText(
        anchor.getTitle()
            + " "
            + (anchor.getContentText() != null ? anchor.getContentText() : ""));
    if (!searchText.trim().isEmpty()) {
      try {
        var fullTextResults = repository.findRelatedByFullTextSearch(anchor.getId(), searchText, limit);
        addUniqueResults(related, addedIds, fullTextResults);
      } catch (Exception e) {
        System.out.println("Full-text search failed: " + e.getMessage());
      }
    }

    if (related.size() < limit) {
      int remaining = limit - related.size();
      try {
        var similarityResults = repository.findRelatedBySimilarity(
            anchor.getId(),
            anchor.getTitle(),
            anchor.getContentText() != null ? anchor.getContentText() : "",
            remaining);
        addUniqueResults(related, addedIds, similarityResults);
      } catch (Exception e) {
        System.out.println("Similarity search failed: " + e.getMessage());
      }
    }

    if (related.size() < limit && anchor.getCategory() != null) {
      int remaining = limit - related.size();
      var categoryResults = repository.findRelatedByCategory(
          StatusPost.PUBLIC,
          anchor.getCategory().getId(),
          anchor.getId(),
          PageRequest.of(0, remaining));
      addUniqueResults(related, addedIds, categoryResults);
    }

    if (related.size() < limit) {
      int remaining = limit - related.size();
      var latestResults = repository.findLatestPosts(
          StatusPost.PUBLIC, anchor.getId(), PageRequest.of(0, remaining));
      addUniqueResults(related, addedIds, latestResults);
    }

    return related.stream()
        .limit(limit)
        .map(
            p -> {
              PostResponse response = mapper.toDTO(p);
              mapAuthorResponse(response, p);
              return response;
            })
        .toList();
  }

  private void addUniqueResults(List<Post> target, Set<Long> addedIds, List<Post> source) {
    for (Post post : source) {
      if (!addedIds.contains(post.getId())) {
        target.add(post);
        addedIds.add(post.getId());
      }
    }
  }

  private String cleanSearchText(String text) {
    if (text == null)
      return "";
    return text.replaceAll("[^\\p{L}\\p{N}\\s]", " ").replaceAll("\\s+", " ").trim();
  }

  @Override
  @Cacheable(value = "posts", key = "'latest:' + #limit")
  public List<PostResponse> getLatestPosts(int limit) {
    Pageable pageable = PageRequest.of(0, Math.min(limit, 50));
    List<Post> posts = repository.findLatestPublicPosts(StatusPost.PUBLIC, pageable);

    return posts.stream()
        .map(
            entity -> {
              PostResponse response = mapper.toDTO(entity);
              mapAuthorResponse(response, entity);
              return response;
            })
        .toList();
  }

  @Override
  public PageResponse<List<PostResponse>> getMyPosts(
      int pageNumber,
      int pageSize,
      List<String> sorts,
      String keyword,
      Long categoryId,
      String status) {
    String kw = (keyword == null) ? "" : keyword.toLowerCase();
    List<String> whiteListFieldSorts = List.of("createdAt", "updatedAt");
    Pageable pageable = AppUtils.generatePageableWithSort(sorts, whiteListFieldSorts, pageNumber, pageSize);

    Long employerId = AppUtils.getEmployerIdFromSecurityContext();
    Page<Post> page;
    if (status == null || status.isBlank()) {
      page = repository.searchPostsByEmployer(kw, categoryId, employerId, pageable);
    } else {
      StatusPost s = StatusPost.fromValue(status);
      page = repository.searchPostsByEmployerAndStatus(kw, categoryId, employerId, s, pageable);
    }

    List<PostResponse> items = page.getContent().stream()
        .map(entity -> {
          PostResponse response = mapper.toDTO(entity);
          mapAuthorResponse(response, entity);
          return response;
        }).toList();

    return PageResponse.<List<PostResponse>>builder()
        .pageNumber(pageNumber)
        .pageSize(pageSize)
        .totalPages(page.getTotalPages())
        .numberOfElements(page.getNumberOfElements())
        .items(items)
        .build();
  }

  @Override
  public PostResponse updateStatus(Long id, String status) {
    Post entity = findPostById(id);
    entity.setStatus(StatusPost.fromValue(status));
    repository.save(entity);
    PostResponse response = mapper.toDTO(entity);
    mapAuthorResponse(response, entity);
    evictCacheByPattern("posts:pn:*");
    evictCacheByPattern("posts:latest:*");
    evictCacheByPattern("posts:related:" + id + ":*");
    return response;
  }

  private void evictCacheByPattern(String pattern) {
    redisUtils.evictCacheByPattern(pattern);
  }

  private void mapAuthorResponse(PostResponse response, Post entity) {
    if (entity.getUserAuthor() != null) {
      response.setUserAuthor(
          UserSummaryResponse.builder()
              .id(entity.getUserAuthor().getId())
              .avatarUrl(entity.getUserAuthor().getAvatarUrl())
              .email(entity.getUserAuthor().getEmail())
              .fullName(entity.getUserAuthor().getFullName())
              .role(entity.getUserAuthor().getRole().getRole().getName())
              .build());
    } else if (entity.getEmployerAuthor() != null) {
      response.setEmployerAuthor(
          EmployerSummaryResponse.builder()
              .id(entity.getEmployerAuthor().getId())
              .email(entity.getEmployerAuthor().getEmail())
              .backgroundUrl(entity.getEmployerAuthor().getBackgroundUrl())
              .avatarUrl(entity.getEmployerAuthor().getAvatarUrl())
              .companyName(entity.getEmployerAuthor().getCompanyName())
              .createdAt(entity.getEmployerAuthor().getCreatedAt())
              .updatedAt(entity.getEmployerAuthor().getUpdatedAt())
              .employerSlug(entity.getEmployerAuthor().getEmployerSlug())
              .build());
    }
  }

  private void validateOwner(Post entity) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (!AppUtils.hasRole(auth, UserRole.ADMIN.getName())) {
      if (entity.getEmployerAuthor() == null
          || !entity
              .getEmployerAuthor()
              .getId()
              .equals(AppUtils.getEmployerIdFromSecurityContext())) {
        throw new AccessDeniedException("Access is denied");
      }
    }
  }

  private void validateOwnerAndPublic(Post entity) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (!entity.getStatus().equals(StatusPost.PUBLIC)
        && !AppUtils.hasRole(auth, UserRole.ADMIN.getName())) {
      if (entity.getEmployerAuthor() == null
          || !entity
              .getEmployerAuthor()
              .getId()
              .equals(AppUtils.getEmployerIdFromSecurityContext())) {
        throw new AccessDeniedException("Access is denied");
      }
    }
  }

  private Post findPostById(Long id) {
    return repository
        .findById(id)
        .orElseThrow(
            () -> new ResourceNotFoundException(
                messageSource.getMessage(
                    "error.resource.not.found", null, LocaleContextHolder.getLocale())));
  }
}
