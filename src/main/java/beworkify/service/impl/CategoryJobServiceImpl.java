package beworkify.service.impl;

import beworkify.dto.request.CategoryJobRequest;
import beworkify.dto.response.CategoryJobResponse;
import beworkify.dto.response.PageResponse;
import beworkify.dto.response.PopularIndustryResponse;
import beworkify.entity.CategoryJob;
import beworkify.entity.Industry;
import beworkify.exception.ResourceConflictException;
import beworkify.exception.ResourceNotFoundException;
import beworkify.mapper.CategoryJobMapper;
import beworkify.repository.CategoryJobRepository;
import beworkify.service.CategoryJobService;
import beworkify.util.AppUtils;
import beworkify.util.RedisUtils;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryJobServiceImpl implements CategoryJobService {

  private final CategoryJobRepository repository;
  private final CategoryJobMapper mapper;
  private final MessageSource messageSource;
  private final RedisUtils redisUtils;

  @Override
  @Transactional
  @Caching(
      evict = {
        @CacheEvict(value = "categories_job", key = "'all'"),
        @CacheEvict(value = "categories_job_count", key = "'industries'")
      },
      put = {@CachePut(value = "categories_job", key = "#result.id")})
  public CategoryJobResponse create(CategoryJobRequest request) {
    if (repository.existsByName(request.getName())) {
      String message =
          messageSource.getMessage(
              "jobCategory.exists.name",
              new Object[] {request.getName()},
              LocaleContextHolder.getLocale());
      throw new ResourceConflictException(message);
    }
    CategoryJob entity = mapper.toEntity(request);
    repository.save(entity);
    evictPaginationCache();
    return mapper.toDTO(entity);
  }

  @Override
  @Transactional
  @Caching(
      evict = {
        @CacheEvict(value = "categories_job", key = "'all'"),
        @CacheEvict(value = "categories_job_count", key = "'industries'")
      },
      put = {@CachePut(value = "categories_job", key = "#result.id")})
  public CategoryJobResponse update(Long id, CategoryJobRequest request) {
    CategoryJob entity = findById(id);

    if (request.getName() != null && repository.existsByNameAndIdNot(request.getName(), id)) {
      String message =
          messageSource.getMessage(
              "jobCategory.exists.name",
              new Object[] {request.getName()},
              LocaleContextHolder.getLocale());
      throw new ResourceConflictException(message);
    }

    mapper.updateEntityFromRequest(request, entity);
    repository.save(entity);
    evictPaginationCache();
    return mapper.toDTO(entity);
  }

  @Override
  @Transactional
  @Caching(
      evict = {
        @CacheEvict(value = "categories_job", key = "'all'"),
        @CacheEvict(value = "categories_job", key = "#id"),
        @CacheEvict(value = "categories_job_count", key = "'industries'")
      })
  public void delete(Long id) {
    CategoryJob entity = findById(id);
    repository.delete(entity);
    evictPaginationCache();
  }

  @Override
  @Cacheable(value = "categories_job", key = "#id")
  public CategoryJobResponse getById(Long id) {
    CategoryJob entity = findById(id);
    return mapper.toDTO(entity);
  }

  @Override
  @Cacheable(
      value = "categories_job",
      key =
          "@keyGenerator.buildKeyWithPaginationSortsKeyword(#pageNumber, #pageSize, #sorts, #keyword, T(java.util.List).of(\"name\", \"createdAt\", \"updatedAt\"))")
  public PageResponse<List<CategoryJobResponse>> getAllWithPaginationAndSort(
      int pageNumber, int pageSize, List<String> sorts, String keyword) {
    String kw = (keyword == null) ? "" : keyword.toLowerCase();
    Pageable pageable =
        AppUtils.generatePageableWithSort(
            sorts, List.of("name", "createdAt", "updatedAt"), pageNumber, pageSize);
    Page<CategoryJob> page = repository.searchJobCategories(kw, pageable);
    List<CategoryJobResponse> items =
        page.getContent().stream().map(mapper::toDTO).collect(Collectors.toList());
    return PageResponse.<List<CategoryJobResponse>>builder()
        .pageNumber(pageNumber)
        .pageSize(pageSize)
        .totalPages(page.getTotalPages())
        .numberOfElements(page.getNumberOfElements())
        .items(items)
        .build();
  }

  @Override
  @Cacheable(value = "categories_job", key = "'all'")
  public List<CategoryJobResponse> getAll() {
    List<CategoryJob> list = repository.findAll(Sort.by(Sort.Direction.ASC, "name"));
    return list.stream().map(mapper::toDTO).collect(Collectors.toList());
  }

  public CategoryJob findById(Long id) {
    return repository
        .findById(id)
        .orElseThrow(
            () -> {
              String message =
                  messageSource.getMessage(
                      "jobCategory.notFound", null, LocaleContextHolder.getLocale());
              return new ResourceNotFoundException(message);
            });
  }

  @Override
  @Cacheable(value = "categories_job_count", key = "'industries'")
  public List<CategoryJobResponse> getCategoriesJobWithCountJobIndustry() {
    List<Object[]> resultQuery = repository.findCategoryJobWithIndustryCount();
    Map<Long, CategoryJobResponse> map = new LinkedHashMap<>();
    resultQuery.forEach(
        r -> {
          CategoryJob c = (CategoryJob) r[0];
          Industry i = (Industry) r[1];
          Long countJob = (Long) r[2];
          map.computeIfAbsent(
              c.getId(),
              id ->
                  CategoryJobResponse.builder()
                      .id(c.getId())
                      .createdAt(c.getCreatedAt())
                      .updatedAt(c.getUpdatedAt())
                      .name(c.getName())
                      .engName(c.getEngName())
                      .description(c.getDescription())
                      .industries(new ArrayList<>())
                      .build());
          if (i != null)
            map.get(c.getId())
                .getIndustries()
                .add(
                    PopularIndustryResponse.builder()
                        .id(i.getId())
                        .name(i.getName())
                        .engName(i.getEngName())
                        .description(i.getDescription())
                        .jobCount(countJob)
                        .build());
        });
    return map.values().stream().toList();
  }

  private void evictPaginationCache() {
    redisUtils.evictCacheByPattern("categories_job:pn:*");
  }
}
