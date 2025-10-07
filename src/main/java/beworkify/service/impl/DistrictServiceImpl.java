package beworkify.service.impl;

import beworkify.dto.request.DistrictRequest;
import beworkify.dto.response.DistrictResponse;
import beworkify.entity.District;
import beworkify.entity.Province;
import beworkify.exception.ResourceConflictException;
import beworkify.exception.ResourceNotFoundException;
import beworkify.mapper.DistrictMapper;
import beworkify.repository.DistrictRepository;
import beworkify.repository.ProvinceRepository;
import beworkify.service.DistrictService;
import lombok.RequiredArgsConstructor;
import beworkify.util.AppUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DistrictServiceImpl implements DistrictService {

    private final DistrictRepository repository;
    private final ProvinceRepository provinceRepository;
    private final DistrictMapper mapper;
    private final MessageSource messageSource;

    @Override
    @Transactional
    public DistrictResponse create(DistrictRequest request) {
        if (repository.existsByCode(request.getCode())) {
            String message = messageSource.getMessage("district.exists", new Object[] { request.getCode() },
                    LocaleContextHolder.getLocale());
            throw new ResourceConflictException(message);
        }
        Province province = provinceRepository.findById(request.getProvinceId()).orElseThrow(() -> {
            String message = messageSource.getMessage("province.notFound", null, LocaleContextHolder.getLocale());
            return new ResourceNotFoundException(message);
        });
        District entity = mapper.toEntity(request);
        entity.setProvince(province);
        entity.setDistrictSlug(AppUtils.toSlug(entity.getName()));
        repository.save(entity);
        return mapper.toDTO(entity);
    }

    @Override
    @Transactional
    public DistrictResponse update(Long id, DistrictRequest request) {
        District entity = repository.findById(id).orElseThrow(() -> {
            String message = messageSource.getMessage("district.notFound", null, LocaleContextHolder.getLocale());
            return new ResourceNotFoundException(message);
        });

        if (request.getCode() != null && repository.existsByCodeAndIdNot(request.getCode(), id)) {
            String message = messageSource.getMessage("district.exists", new Object[] { request.getCode() },
                    LocaleContextHolder.getLocale());
            throw new ResourceConflictException(message);
        }

        if (request.getProvinceId() != null) {
            Province province = provinceRepository.findById(request.getProvinceId()).orElseThrow(() -> {
                String message = messageSource.getMessage("province.notFound", null,
                        LocaleContextHolder.getLocale());
                return new ResourceNotFoundException(message);
            });
            entity.setProvince(province);
        }

        mapper.updateEntityFromRequest(request, entity);
        entity.setDistrictSlug(AppUtils.toSlug(entity.getName()));
        repository.save(entity);
        return mapper.toDTO(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        District entity = repository.findById(id).orElseThrow(() -> {
            String message = messageSource.getMessage("district.notFound", null, LocaleContextHolder.getLocale());
            return new ResourceNotFoundException(message);
        });
        repository.delete(entity);
    }

    @Override
    public DistrictResponse getById(Long id) {
        District entity = repository.findById(id).orElseThrow(() -> {
            String message = messageSource.getMessage("district.notFound", null, LocaleContextHolder.getLocale());
            return new ResourceNotFoundException(message);
        });
        return mapper.toDTO(entity);
    }

    @Override
    public District findDistrictById(Long id) {
        return repository.findById(id).orElseThrow(() -> {
            String message = messageSource.getMessage("district.not.found", null,
                    LocaleContextHolder.getLocale());
            return new ResourceNotFoundException(message);
        });
    }

    @Override
    public List<DistrictResponse> getAll() {
        return repository.findAllByOrderByNameAsc().stream().map(mapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<DistrictResponse> getByProvinceId(Long provinceId) {
        return repository.findAllByProvinceIdOrderByNameAsc(provinceId).stream().map(mapper::toDTO)
                .collect(Collectors.toList());
    }
}