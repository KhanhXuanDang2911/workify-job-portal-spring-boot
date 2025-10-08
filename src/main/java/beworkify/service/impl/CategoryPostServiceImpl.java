package beworkify.service.impl;

import beworkify.dto.request.CategoryPostRequest;
import beworkify.dto.response.CategoryPostResponse;
import beworkify.dto.response.PageResponse;
import beworkify.entity.CategoryPost;
import beworkify.exception.ResourceConflictException;
import beworkify.exception.ResourceNotFoundException;
import beworkify.mapper.CategoryPostMapper;
import beworkify.repository.CategoryPostRepository;
import beworkify.service.CategoryPostService;
import beworkify.util.AppUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryPostServiceImpl implements CategoryPostService {

    private final CategoryPostRepository repository;
    private final CategoryPostMapper mapper;
    private final MessageSource messageSource;

    @Override
    @Transactional
    public CategoryPostResponse create(CategoryPostRequest request) {
        if (repository.existsByTitle(request.getTitle())) {
            String message = messageSource.getMessage("categoryPost.exists", new Object[] { request.getTitle() },
                    LocaleContextHolder.getLocale());
            throw new ResourceConflictException(message);
        }
        String slug = AppUtils.toSlug(request.getTitle());
        if (repository.existsBySlug(slug)) {
            String message = messageSource.getMessage("categoryPost.exists", new Object[] { request.getTitle() },
                    LocaleContextHolder.getLocale());
            throw new ResourceConflictException(message);
        }
        CategoryPost entity = mapper.toEntity(request);
        entity.setSlug(slug);
        repository.save(entity);
        return mapper.toDTO(entity);
    }

    @Override
    @Transactional
    public CategoryPostResponse update(Long id, CategoryPostRequest request) {
        CategoryPost entity = repository.findById(id)
                .orElseThrow(() -> {
                    String message = messageSource.getMessage("categoryPost.notFound", null,
                            LocaleContextHolder.getLocale());
                    return new ResourceNotFoundException(message);
                });

        if (request.getTitle() != null && repository.existsByTitleAndIdNot(request.getTitle(), id)) {
            String message = messageSource.getMessage("categoryPost.exists", new Object[] { request.getTitle() },
                    LocaleContextHolder.getLocale());
            throw new ResourceConflictException(message);
        }

        mapper.updateEntityFromRequest(request, entity);
        if (request.getTitle() != null) {
            entity.setSlug(AppUtils.toSlug(request.getTitle()));
        }
        repository.save(entity);
        return mapper.toDTO(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        CategoryPost entity = repository.findById(id)
                .orElseThrow(() -> {
                    String message = messageSource.getMessage("categoryPost.notFound", null,
                            LocaleContextHolder.getLocale());
                    return new ResourceNotFoundException(message);
                });
        repository.delete(entity);
    }

    @Override
    public CategoryPostResponse getById(Long id) {
        CategoryPost entity = repository.findById(id)
                .orElseThrow(() -> {
                    String message = messageSource.getMessage("categoryPost.notFound", null,
                            LocaleContextHolder.getLocale());
                    return new ResourceNotFoundException(message);
                });
        return mapper.toDTO(entity);
    }

    @Override
    public PageResponse<List<CategoryPostResponse>> getAllWithPaginationAndSort(int pageNumber, int pageSize,
            List<String> sorts, String keyword) {
        String kw = (keyword == null) ? "" : keyword.toLowerCase();
        org.springframework.data.domain.Pageable pageable = AppUtils.generatePageableWithSort(sorts,
                List.of("title", "createdAt", "updatedAt"), pageNumber, pageSize);
        Page<CategoryPost> page = repository.searchCategories(kw, pageable);
        List<CategoryPostResponse> items = page.getContent().stream().map(mapper::toDTO).collect(Collectors.toList());
        return PageResponse.<List<CategoryPostResponse>>builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .totalPages(page.getTotalPages())
                .numberOfElements(page.getNumberOfElements())
                .items(items)
                .build();
    }

    @Override
    public List<CategoryPostResponse> getAll() {
        List<CategoryPost> list = repository.findAll(Sort.by(Sort.Direction.ASC, "title"));
        return list.stream().map(mapper::toDTO).collect(Collectors.toList());
    }
}
