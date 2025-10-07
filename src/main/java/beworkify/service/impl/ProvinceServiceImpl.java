package beworkify.service.impl;

import beworkify.dto.request.ProvinceRequest;
import beworkify.dto.response.ProvinceResponse;
import beworkify.dto.response.PageResponse;
import beworkify.entity.Province;
import beworkify.exception.ResourceConflictException;
import beworkify.exception.ResourceNotFoundException;
import beworkify.mapper.ProvinceMapper;
import beworkify.repository.ProvinceRepository;
import beworkify.service.ProvinceService;
import beworkify.util.AppUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProvinceServiceImpl implements ProvinceService {

    private final ProvinceRepository repository;
    private final ProvinceMapper mapper;
    private final MessageSource messageSource;

    @Override
    @Transactional
    public ProvinceResponse create(ProvinceRequest request) {
        if (repository.existsByCode(request.getCode())) {
            String message = messageSource.getMessage("province.exists", new Object[] { request.getCode() },
                    LocaleContextHolder.getLocale());
            throw new ResourceConflictException(message);
        }
        Province entity = mapper.toEntity(request);
        entity.setProvinceSlug(AppUtils.toSlug(entity.getName()));
        repository.save(entity);
        return mapper.toDTO(entity);
    }

    @Override
    @Transactional
    public ProvinceResponse update(Long id, ProvinceRequest request) {
        Province entity = repository.findById(id)
                .orElseThrow(() -> {
                    String message = messageSource.getMessage("province.notFound", null,
                            LocaleContextHolder.getLocale());
                    return new ResourceNotFoundException(message);
                });

        if (request.getCode() != null && repository.existsByCodeAndIdNot(request.getCode(), id)) {
            String message = messageSource.getMessage("province.exists", new Object[] { request.getCode() },
                    LocaleContextHolder.getLocale());
            throw new ResourceConflictException(message);
        }

        mapper.updateEntityFromRequest(request, entity);
        entity.setProvinceSlug(AppUtils.toSlug(entity.getName()));
        repository.save(entity);
        return mapper.toDTO(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Province entity = repository.findById(id)
                .orElseThrow(() -> {
                    String message = messageSource.getMessage("province.notFound", null,
                            LocaleContextHolder.getLocale());
                    return new ResourceNotFoundException(message);
                });
        repository.delete(entity);
    }

    @Override
    public ProvinceResponse getById(Long id) {
        Province entity = repository.findById(id)
                .orElseThrow(() -> {
                    String message = messageSource.getMessage("province.notFound", null,
                            LocaleContextHolder.getLocale());
                    return new ResourceNotFoundException(message);
                });
        return mapper.toDTO(entity);
    }

    @Override
    public PageResponse<List<ProvinceResponse>> getAllWithPaginationAndSort(int pageNumber, int pageSize,
            List<String> sorts, String keyword) {
        String kw = (keyword == null) ? "" : keyword.toLowerCase();
        Pageable pageable = AppUtils.generatePageableWithSort(sorts,
                List.of("name", "createdAt", "updatedAt"), pageNumber, pageSize);
        Page<Province> page = repository.searchProvinces(kw, pageable);
        List<ProvinceResponse> items = page.getContent().stream().map(mapper::toDTO).collect(Collectors.toList());
        return PageResponse.<List<ProvinceResponse>>builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .totalPages(page.getTotalPages())
                .numberOfElements(page.getNumberOfElements())
                .items(items)
                .build();
    }

    @Override
    public Province findProvinceById(Long id) {
        return repository.findById(id).orElseThrow(() -> {
            String message = messageSource.getMessage("province.not.found", null,
                    LocaleContextHolder.getLocale());
            return new ResourceNotFoundException(message);
        });
    }

    @Override
    public List<ProvinceResponse> getAll() {
        List<Province> list = repository.findAllByOrderByNameAsc();
        return list.stream().map(mapper::toDTO).collect(Collectors.toList());
    }
}
