package beworkify.service.impl;

import beworkify.dto.request.PostRequest;
import beworkify.dto.response.PageResponse;
import beworkify.dto.response.PostResponse;
import beworkify.dto.response.UserSummaryResponse;
import beworkify.entity.CategoryPost;
import beworkify.entity.Post;
import beworkify.entity.User;
import beworkify.enumeration.StatusPost;
import beworkify.exception.ResourceNotFoundException;
import beworkify.mapper.PostMapper;
import beworkify.repository.CategoryPostRepository;
import beworkify.repository.PostRepository;
import beworkify.repository.UserRepository;
import beworkify.service.AzureBlobService;
import beworkify.service.PostService;
import beworkify.util.AppUtils;
import beworkify.util.HtmlImageProcessor;
import beworkify.util.TagUtils;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository repository;
    private final PostMapper mapper;
    private final CategoryPostRepository categoryRepository;
    private final UserRepository userRepository;
    private final AzureBlobService storageService;
    private final MessageSource messageSource;

    @Override
    @Transactional
    public PostResponse create(PostRequest request, MultipartFile thumbnail) throws Exception {
        Post entity = mapper.toEntity(request);
        CategoryPost category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageSource.getMessage("categoryPost.notFound", null, LocaleContextHolder.getLocale())));
        entity.setCategory(category);
        User author;
        Long id = AppUtils.getUserIdFromSecurityContext();
        author = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage("user.not.found.by.id",
                        new Object[] { id }, LocaleContextHolder.getLocale())));
        entity.setAuthor(author);
        HtmlImageProcessor processor = new HtmlImageProcessor(storageService);
        String processedContent = processor.process(request.getContent());
        entity.setContent(processedContent);
        entity.setContentText(Jsoup.parse(processedContent).text());
        String tags = TagUtils.extractTagsAsPipe(processedContent);
        entity.setTags(tags);
        int words = AppUtils.countWords(processedContent);
        entity.setReadingTimeMinutes(Math.max(1, words / 200));
        entity.setSlug(AppUtils.toSlug(entity.getTitle()));
        entity.setStatus(StatusPost.fromValue(request.getStatus()));
        if (thumbnail != null && !thumbnail.isEmpty()) {
            String thumbnailUrl = storageService.uploadFile(thumbnail);
            entity.setThumbnailUrl(thumbnailUrl);
        }
        repository.save(entity);
        PostResponse response = mapper.toDTO(entity);
        response.setAuthor(UserSummaryResponse.builder()
                .id(entity.getAuthor().getId())
                .avatarUrl(entity.getAuthor().getAvatarUrl())
                .email(entity.getAuthor().getEmail())
                .fullName(entity.getAuthor().getFullName())
                .role(entity.getAuthor().getRole().getRole().getName())
                .build());
        return response;
    }

    @Override
    @Transactional
    public PostResponse update(Long id, PostRequest request, MultipartFile thumbnail) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Post entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageSource.getMessage("error.resource.not.found", null, LocaleContextHolder.getLocale())));
        mapper.updateEntityFromRequest(request, entity);
        if (request.getCategoryId() != null) {
            CategoryPost category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            messageSource.getMessage("categoryPost.notFound", null, LocaleContextHolder.getLocale())));
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

        if (request.getStatus() != null && AppUtils.hasRole(auth, "ADMIN")) {
            entity.setStatus(StatusPost.fromValue(request.getStatus()));
        }

        repository.save(entity);
        PostResponse response = mapper.toDTO(entity);
        response.setAuthor(UserSummaryResponse.builder()
                .id(entity.getAuthor().getId())
                .avatarUrl(entity.getAuthor().getAvatarUrl())
                .email(entity.getAuthor().getEmail())
                .fullName(entity.getAuthor().getFullName())
                .role(entity.getAuthor().getRole().getRole().getName())
                .build());
        return response;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Post entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageSource.getMessage("error.resource.not.found", null, LocaleContextHolder.getLocale())));
        repository.delete(entity);
    }

    @Override
    @PostAuthorize("hasRole('ADMIN') " +
            "or returnObject.status == T(beworkify.enumeration.StatusPost).PUBLIC")
    public PostResponse getById(Long id) {
        Post entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageSource.getMessage("error.resource.not.found", null, LocaleContextHolder.getLocale())));
        PostResponse response = mapper.toDTO(entity);
        response.setAuthor(UserSummaryResponse.builder()
                .id(entity.getAuthor().getId())
                .avatarUrl(entity.getAuthor().getAvatarUrl())
                .fullName(entity.getAuthor().getFullName())
                .email(entity.getAuthor().getEmail())
                .role(entity.getAuthor().getRole().getRole().getName())
                .build());
        return response;
    }

    @Override
    public PageResponse<List<PostResponse>> getAll(int pageNumber, int pageSize, List<String> sorts, String keyword,
            Long categoryId, boolean isPublic, Long authorId) {
        String kw = (keyword == null) ? "" : keyword.toLowerCase();
        List<String> whiteListFieldSorts = List.of("createdAt", "updatedAt");
        Pageable pageable = AppUtils.generatePageableWithSort(sorts, whiteListFieldSorts, pageNumber, pageSize);
        Page<Post> page;
        if (!isPublic) {
            page = repository.searchPosts(kw, categoryId, authorId, pageable);
        } else {
            page = repository.searchPublicPosts(kw, categoryId, StatusPost.PUBLIC, pageable);
        }
        List<PostResponse> items = page.getContent().stream().map(entity -> {
            PostResponse response = mapper.toDTO(entity);
            response.setAuthor(UserSummaryResponse.builder()
                    .id(entity.getAuthor().getId())
                    .avatarUrl(entity.getAuthor().getAvatarUrl())
                    .fullName(entity.getAuthor().getFullName())
                    .role(entity.getAuthor().getRole().getRole().getName())
                    .email(entity.getAuthor().getEmail())
                    .build());
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
    public List<PostResponse> getRelated(Long postId, int limit) {
        Post anchor = repository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageSource.getMessage("error.resource.not.found", null, LocaleContextHolder.getLocale())));

        List<Post> related = new ArrayList<>();
        Set<Long> addedIds = new HashSet<>();

        String searchText = cleanSearchText(anchor.getTitle() + " " +
                (anchor.getContentText() != null ? anchor.getContentText() : ""));
        if (!searchText.trim().isEmpty()) {
            try {
                var fullTextResults = repository.findRelatedByFullTextSearch(
                        anchor.getId(), searchText, limit);
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
                    StatusPost.PUBLIC,
                    anchor.getId(),
                    PageRequest.of(0, remaining));
            addUniqueResults(related, addedIds, latestResults);
        }

        return related.stream()
                .limit(limit)
                .map(p -> {
                    PostResponse response = mapper.toDTO(p);
                    response.setAuthor(UserSummaryResponse.builder()
                            .id(p.getAuthor().getId())
                            .avatarUrl(p.getAuthor().getAvatarUrl())
                            .fullName(p.getAuthor().getFullName())
                            .role(p.getAuthor().getRole().getRole().getName())
                            .email(p.getAuthor().getEmail())
                            .build());
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
        return text.replaceAll("[^\\p{L}\\p{N}\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    @Override
    public List<PostResponse> getLatestPosts(int limit) {
        Pageable pageable = PageRequest.of(0,
                Math.min(limit, 50));
        List<Post> posts = repository.findLatestPublicPosts(StatusPost.PUBLIC, pageable);

        return posts.stream()
                .map(entity -> {
                    PostResponse response = mapper.toDTO(entity);
                    response.setAuthor(UserSummaryResponse.builder()
                            .id(entity.getAuthor().getId())
                            .avatarUrl(entity.getAuthor().getAvatarUrl())
                            .fullName(entity.getAuthor().getFullName())
                            .role(entity.getAuthor().getRole().getRole().getName())
                            .email(entity.getAuthor().getEmail())
                            .build());
                    return response;
                })
                .toList();
    }

}