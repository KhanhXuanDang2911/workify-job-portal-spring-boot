package beworkify.service.impl;

import beworkify.dto.request.IndustryRequest;
import beworkify.dto.response.IndustryResponse;
import beworkify.dto.response.PageResponse;
import beworkify.entity.Industry;
import beworkify.exception.ResourceConflictException;
import beworkify.exception.ResourceNotFoundException;
import beworkify.mapper.IndustryMapper;
import beworkify.repository.IndustryRepository;
import beworkify.service.IndustryService;
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
public class IndustryServiceImpl implements IndustryService {

    private final IndustryRepository repository;
    private final IndustryMapper mapper;
    private final MessageSource messageSource;

    @Override
    @Transactional
    public IndustryResponse create(IndustryRequest request) {
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
        repository.save(entity);
        return mapper.toDTO(entity);
    }

    @Override
    @Transactional
    public IndustryResponse update(Long id, IndustryRequest request) {
        Industry entity = repository.findById(id)
                .orElseThrow(() -> {
                    String message = messageSource.getMessage("industry.notFound", null,
                            LocaleContextHolder.getLocale());
                    return new ResourceNotFoundException(message);
                });

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
        repository.save(entity);
        return mapper.toDTO(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Industry entity = repository.findById(id)
                .orElseThrow(() -> {
                    String message = messageSource.getMessage("industry.notFound", null,
                            LocaleContextHolder.getLocale());
                    return new ResourceNotFoundException(message);
                });
        repository.delete(entity);
    }

    @Override
    public IndustryResponse getById(Long id) {
        Industry entity = repository.findById(id)
                .orElseThrow(() -> {
                    String message = messageSource.getMessage("industry.notFound", null,
                            LocaleContextHolder.getLocale());
                    return new ResourceNotFoundException(message);
                });
        return mapper.toDTO(entity);
    }

    @Override
    public PageResponse<List<IndustryResponse>> getAllWithPaginationAndSort(int pageNumber, int pageSize,
            List<String> sorts, String keyword) {
        String kw = (keyword == null) ? "" : keyword.toLowerCase();
        org.springframework.data.domain.Pageable pageable = AppUtils.generatePageableWithSort(sorts,
                List.of("name", "engName", "createdAt", "updatedAt"), pageNumber, pageSize);
        Page<Industry> page = repository.searchIndustries(kw, pageable);
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
    public List<IndustryResponse> getAll() {
        List<Industry> list = repository.findAll(Sort.by(Sort.Direction.ASC, "name"));
        return list.stream().map(mapper::toDTO).collect(Collectors.toList());
    }
}
