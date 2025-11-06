package beworkify.service.impl;

import beworkify.dto.request.IndustryRequest;
import beworkify.dto.response.IndustryResponse;
import beworkify.dto.response.PageResponse;
import beworkify.entity.CategoryJob;
import beworkify.entity.Industry;
import beworkify.exception.ResourceConflictException;
import beworkify.exception.ResourceNotFoundException;
import beworkify.mapper.IndustryMapper;
import beworkify.repository.IndustryRepository;
import beworkify.service.CategoryJobService;
import beworkify.service.IndustryService;
import beworkify.util.AppUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IndustryServiceImpl implements IndustryService {

    private final IndustryRepository repository;
    private final IndustryMapper mapper;
    private final MessageSource messageSource;
    private final CategoryJobService categoryJobService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "industries", key = "'all'")
    }, put = {
            @CachePut(value = "industries", key = "#result.id")
    })
    public IndustryResponse create(IndustryRequest request) {
        CategoryJob categoryJob = categoryJobService.findById(request.getCategoryJobId());
        if (repository.existsByName(request.getName())) {
            String message = messageSource.getMessage("industry.exists.name", new Object[] { request.getName() },
                    LocaleContextHolder.getLocale());
            throw new ResourceConflictException(message);
        }
        if (repository.existsByEngName(request.getEngName())) {
            String message = messageSource.getMessage("industry.exists.engName",
                    new Object[] { request.getEngName() }, LocaleContextHolder.getLocale());
            throw new ResourceConflictException(message);
        }
        Industry entity = mapper.toEntity(request);
        entity.setCategoryJob(categoryJob);
        repository.save(entity);
        evictPaginationCache();
        return mapper.toDTO(entity);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "industries", key = "'all'")
    }, put = {
            @CachePut(value = "industries", key = "#id")
    })
    public IndustryResponse update(Long id, IndustryRequest request) {
        Industry entity = findIndustryById(id);

        CategoryJob categoryJob = categoryJobService.findById(request.getCategoryJobId());

        if (request.getName() != null && repository.existsByNameAndIdNot(request.getName(), id)) {
            String message = messageSource.getMessage("industry.exists.name", new Object[] { request.getName() },
                    LocaleContextHolder.getLocale());
            throw new ResourceConflictException(message);
        }
        if (request.getEngName() != null && repository.existsByEngNameAndIdNot(request.getEngName(), id)) {
            String message = messageSource.getMessage("industry.exists.engName",
                    new Object[] { request.getEngName() }, LocaleContextHolder.getLocale());
            throw new ResourceConflictException(message);
        }

        mapper.updateEntityFromRequest(request, entity);
        entity.setCategoryJob(categoryJob);
        repository.save(entity);
        evictPaginationCache();
        return mapper.toDTO(entity);
    }

    @Override
    @Transactional
    @CacheEvict(value="industries", allEntries = true)
    public void delete(Long id) {
        Industry entity = findIndustryById(id);
        repository.delete(entity);
    }

    @Override
    public Industry findIndustryById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> {
                    String message = messageSource.getMessage("industry.notFound", null,
                            LocaleContextHolder.getLocale());
                    return new ResourceNotFoundException(message);
                });
    }

    @Override
    @Cacheable(value="industries", key="#id")
    public IndustryResponse getById(Long id) {
        Industry entity = findIndustryById(id);
        return mapper.toDTO(entity);
    }

    @Override
    @Cacheable(value = "industries", key = "@keyGenerator.buildKeyWithPaginationSortsKeywordForIndustries(#pageNumber, #pageSize, #sorts, #keyword, T(java.util.List).of(\"name\", \"engName\", \"createdAt\", \"updatedAt\"), #categoryId)")
    public PageResponse<List<IndustryResponse>> getAllWithPaginationAndSort(int pageNumber, int pageSize,
            List<String> sorts, String keyword, Long categoryId) {
        String kw = (keyword == null) ? "" : keyword.toLowerCase();
        org.springframework.data.domain.Pageable pageable = AppUtils.generatePageableWithSort(sorts,
                List.of("name", "engName", "createdAt", "updatedAt"), pageNumber, pageSize);
        Page<Industry> page = repository.searchIndustries(kw, categoryId, pageable);
        List<IndustryResponse> items = page.getContent().stream().map(mapper::toDTO).collect(Collectors.toList());
        return PageResponse.<List<IndustryResponse>>builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .totalPages(page.getTotalPages())
                .numberOfElements(page.getNumberOfElements())
                .items(items)
                .build();
    }

    @Override
    @Cacheable(value="industries", key="'all'")
    public List<IndustryResponse> getAll() {
        List<Industry> list = repository.findAll(Sort.by(Sort.Direction.ASC, "name"));
        return list.stream().map(mapper::toDTO).collect(Collectors.toList());
    }

    private void evictPaginationCache(){
        Set<String> keys = redisTemplate.keys("industries:pn:*");
        if (!keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

}
