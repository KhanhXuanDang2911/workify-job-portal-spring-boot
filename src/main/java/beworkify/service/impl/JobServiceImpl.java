package beworkify.service.impl;

import beworkify.dto.request.JobRequest;
import beworkify.dto.request.LocationRequest;
import beworkify.dto.response.*;
import beworkify.entity.*;
import beworkify.enumeration.JobStatus;
import beworkify.exception.ResourceNotFoundException;
import beworkify.mapper.IndustryMapper;
import beworkify.mapper.JobMapper;
import beworkify.mapper.ProvinceMapper;
import beworkify.repository.*;
import beworkify.service.JobService;
import beworkify.util.AppUtils;
import beworkify.search.service.JobSearchService;
import beworkify.util.RedisUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

        private final JobRepository jobRepository;
        private final LocationRepository locationRepository;
        private final IndustryRepository industryRepository;
        private final ProvinceRepository provinceRepository;
        private final DistrictRepository districtRepository;
        private final JobMapper mapper;
        private final MessageSource messageSource;
        private final EmployerRepository employerRepository;
        private final ProvinceMapper provinceMapper;
        private final IndustryMapper industryMapper;
        private final JobSearchService jobSearchService;
        private final RedisUtils redisUtils;

        @Override
        @Transactional
        public JobResponse create(JobRequest request) {
                Job entity = mapper.toEntity(request);
                Location contactLocation = createLocationFromRequest(request.getContactLocation());
                entity.setContactLocation(contactLocation);
                List<Location> jobLocations = createJobLocationsFromRequest(request.getJobLocations(), entity);
                if (entity.getJobLocations() == null) {
                        entity.setJobLocations(new HashSet<>());
                }
                entity.getJobLocations().addAll(jobLocations);
                List<JobIndustry> jobIndustries = createJobIndustriesFromRequest(request.getIndustryIds(), entity);
                if (entity.getJobIndustries() == null) {
                        entity.setJobIndustries(new HashSet<>());
                }
                entity.getJobIndustries().addAll(jobIndustries);
                entity.setStatus(JobStatus.PENDING);
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                String emailAuthor = ((UserDetails) authentication.getPrincipal()).getUsername();
                entity.setAuthor(employerRepository.findByEmail(emailAuthor).get());
                entity = jobRepository.save(entity);

                jobSearchService.index(entity);
                JobResponse response = mapper.toDTO(entity);
                evictCacheByPattern("jobs:pn:*");
                evictCacheByPattern("jobs:popular:*");
                return response;
        }

        @Override
        @Transactional
        public JobResponse update(Long id, JobRequest request) {
                Job entity = jobRepository.findById(id)
                                .orElseThrow(() -> {
                                        String message = messageSource.getMessage("job.notFound", null,
                                                        LocaleContextHolder.getLocale());
                                        return new ResourceNotFoundException(message);
                                });
                checkAuthorJob(entity);

                mapper.updateEntityFromRequest(request, entity);

                if (request.getContactLocation() != null) {
                        Location contactLocation = entity.getContactLocation();
                        updateLocationFromRequest(request.getContactLocation(), contactLocation);
                }

                if (request.getJobLocations() != null) {
                        Set<Location> jobLocations = entity.getJobLocations();
                        jobLocations.clear();
                        List<Location> newJobLocations = createJobLocationsFromRequest(request.getJobLocations(),
                                        entity);
                        jobLocations.addAll(newJobLocations);
                }

                if (request.getIndustryIds() != null) {
                        Set<JobIndustry> jobIndustries = entity.getJobIndustries();
                        jobIndustries.clear();

                        List<JobIndustry> newJobIndustries = createJobIndustriesFromRequest(request.getIndustryIds(),
                                        entity);
                        jobIndustries.addAll(newJobIndustries);
                }

                entity = jobRepository.save(entity);

                jobSearchService.index(entity);
                JobResponse response = mapper.toDTO(entity);
                evictCacheByPattern("jobs:pn:*");
                evictCacheByPattern("jobs:popular:*");
                return response;
        }

        @Override
        @Transactional
        public void delete(Long id) {
                Job entity = jobRepository.findById(id)
                                .orElseThrow(() -> {
                                        String message = messageSource.getMessage("job.notFound", null,
                                                        LocaleContextHolder.getLocale());
                                        return new ResourceNotFoundException(message);
                                });
                checkAuthorJob(entity);
                jobRepository.delete(entity);

                jobSearchService.deleteById(entity.getId());
                evictCacheByPattern("jobs:pn:*");
                evictCacheByPattern("jobs:popular:*");
        }

        @Override
        @Transactional(readOnly = true)
        @PostAuthorize("returnObject.status == T(beworkify.enumeration.JobStatus).APPROVED or hasRole('ADMIN') or hasRole('EMPLOYER') and returnObject.author.email == authentication.principal.username")
        public JobResponse getById(Long id) {
                Job entity = jobRepository.findById(id)
                                .orElseThrow(() -> {
                                        String message = messageSource.getMessage("job.notFound", null,
                                                        LocaleContextHolder.getLocale());
                                        return new ResourceNotFoundException(message);
                                });
                return mapper.toDTO(entity);
        }

        @Override
        @Transactional(readOnly = true)
        public PageResponse<List<JobResponse>> getMyJobs(int pageNumber, int pageSize, Long industryId, Long provinceId,
                        List<String> sorts, String keyword) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                String email = ((UserDetails) authentication.getPrincipal()).getUsername();
                List<String> WHITE_LIST_SORTS = Arrays.asList("jobTitle", "createdAt", "updatedAt",
                                "expirationDate", "status");
                keyword = keyword == null ? "" : keyword.trim();
                Pageable pageable = AppUtils.generatePageableWithSort(sorts, WHITE_LIST_SORTS, pageNumber, pageSize);
                Page<Job> page = jobRepository.findMyJobs(provinceId, industryId, keyword.toLowerCase(), email,
                                pageable);
                return toPageResponse(page);
        }

        @Override
        @Cacheable(value = "jobs", key = "@keyGenerator.buildKeyForHiringJobs(#employerId, #pageNumber, #pageSize, #sorts, T(java.util.List).of('createdAt','updatedAt','expirationDate'))")
        public PageResponse<List<JobResponse>> getHiringJobs(Long employerId, int pageNumber, int pageSize,
                        List<String> sorts) {

                List<String> WHITE_LIST_SORTS = Arrays.asList("createdAt", "updatedAt", "expirationDate");
                Pageable pageable = AppUtils.generatePageableWithSort(sorts, WHITE_LIST_SORTS, pageNumber, pageSize);
                Page<Job> page = jobRepository.findHiringJobs(employerId, pageable);
                return toPageResponse(page);
        }

        @Override
        @Cacheable(value = "jobs", key = "@keyGenerator.buildKeyForGetAllJobs(#pageNumber, #pageSize, #industryId, #provinceId ,#sorts, #keyword, T(java.util.List).of('jobTitle','createdAt','updatedAt','expirationDate','status'))")
        public PageResponse<List<JobResponse>> getAllJobs(int pageNumber, int pageSize, Long industryId,
                        Long provinceId, List<String> sorts, String keyword) {
                List<String> WHITE_LIST_SORTS = Arrays.asList("jobTitle", "createdAt", "updatedAt",
                                "expirationDate", "status");
                keyword = keyword == null ? "" : keyword.trim();
                Pageable pageable = AppUtils.generatePageableWithSort(sorts, WHITE_LIST_SORTS, pageNumber, pageSize);
                Page<Job> page = jobRepository.findAllJobs(provinceId, industryId, keyword.toLowerCase(),
                                pageable);
                return toPageResponse(page);
        }

        @Override
        public List<IndustryResponse> getMyCurrentIndustries(Long employerId) {
                List<Industry> industries = jobRepository.findEmployerIndustries(employerId);
                return industryMapper.toDTOs(industries);
        }

        @Override
        public List<ProvinceResponse> getMyCurrentLocations(Long employerId) {
                List<Province> provinces = jobRepository.findEmployerProvinces(employerId);
                return provinceMapper.toDTOs(provinces);
        }

        @Override
        @Cacheable(value = "jobs", key = "'popular:locations:' + #limit")
        public List<PopularLocationResponse> getPopularLocations(Integer limit) {
                List<Object[]> results = jobRepository.getPopularProvinces(limit);
                return results.stream().map(r -> {
                        Province province = (Province) r[0];
                        Long jobCount = (Long) r[1];
                        return PopularLocationResponse.builder()
                                        .id(province.getId())
                                        .code(province.getCode())
                                        .name(province.getName())
                                        .engName(province.getEngName())
                                        .provinceSlug(province.getProvinceSlug())
                                        .jobCount(jobCount)
                                        .build();
                }).toList();
        }

        @Override
        @Cacheable(value = "jobs", key = "'popular:industries:' + #limit")
        public List<PopularIndustryResponse> getPopularIndustries(Integer limit) {
                List<Object[]> results = jobRepository.getPopularIndustries(limit);
                return results.stream().map(r -> {
                        Industry industry = (Industry) r[0];
                        Long jobCount = (Long) r[1];
                        return PopularIndustryResponse.builder()
                                        .id(industry.getId())
                                        .name(industry.getName())
                                        .engName(industry.getEngName())
                                        .description(industry.getDescription())
                                        .jobCount(jobCount)
                                        .build();
                }).toList();
        }

        @Override
        public void closeJob(Long id) {
                Job entity = jobRepository.findById(id)
                                .orElseThrow(() -> {
                                        String message = messageSource.getMessage("job.notFound", null,
                                                        LocaleContextHolder.getLocale());
                                        return new ResourceNotFoundException(message);
                                });
                checkAuthorJob(entity);
                entity.setStatus(JobStatus.CLOSED);
                jobRepository.save(entity);
                jobSearchService.index(entity);
                evictCacheByPattern("jobs:pn:*");
                evictCacheByPattern("jobs:popular:*");
        }

        @Override
        public void updateStatus(Long id, JobStatus jobStatus) {
                Job entity = jobRepository.findById(id)
                                .orElseThrow(() -> {
                                        String message = messageSource.getMessage("job.notFound", null,
                                                        LocaleContextHolder.getLocale());
                                        return new ResourceNotFoundException(message);
                                });
                entity.setStatus(jobStatus);
                jobRepository.save(entity);
                jobSearchService.index(entity);
                evictCacheByPattern("jobs:pn:*");
                evictCacheByPattern("jobs:popular:*");
        }

        private Location createLocationFromRequest(LocationRequest request) {
                Province province = provinceRepository.findById(request.getProvinceId())
                                .orElseThrow(() -> {
                                        String message = messageSource.getMessage("province.not.found", null,
                                                        LocaleContextHolder.getLocale());
                                        return new ResourceNotFoundException(message);
                                });

                District district = districtRepository.findById(request.getDistrictId())
                                .orElseThrow(() -> {
                                        String message = messageSource.getMessage("district.not.found", null,
                                                        LocaleContextHolder.getLocale());
                                        return new ResourceNotFoundException(message);
                                });

                return Location.builder()
                                .province(province)
                                .district(district)
                                .detailAddress(request.getDetailAddress())
                                .build();
        }

        private void updateLocationFromRequest(LocationRequest request, Location location) {
                if (request.getProvinceId() != null) {
                        Province province = provinceRepository.findById(request.getProvinceId())
                                        .orElseThrow(() -> {
                                                String message = messageSource.getMessage("province.not.found", null,
                                                                LocaleContextHolder.getLocale());
                                                return new ResourceNotFoundException(message);
                                        });
                        location.setProvince(province);
                }

                if (request.getDistrictId() != null) {
                        District district = districtRepository.findById(request.getDistrictId())
                                        .orElseThrow(() -> {
                                                String message = messageSource.getMessage("district.not.found", null,
                                                                LocaleContextHolder.getLocale());
                                                return new ResourceNotFoundException(message);
                                        });
                        location.setDistrict(district);
                }

                if (request.getDetailAddress() != null) {
                        location.setDetailAddress(request.getDetailAddress());
                }

                locationRepository.save(location);
        }

        private List<Location> createJobLocationsFromRequest(List<LocationRequest> requests,
                        Job job) {
                List<Location> locations = new ArrayList<>();
                for (LocationRequest request : requests) {
                        Province province = provinceRepository.findById(request.getProvinceId())
                                        .orElseThrow(() -> {
                                                String message = messageSource.getMessage("province.not.found", null,
                                                                LocaleContextHolder.getLocale());
                                                return new ResourceNotFoundException(message);
                                        });

                        District district = districtRepository.findById(request.getDistrictId())
                                        .orElseThrow(() -> {
                                                String message = messageSource.getMessage("district.not.found", null,
                                                                LocaleContextHolder.getLocale());
                                                return new ResourceNotFoundException(message);
                                        });

                        Location location = Location.builder()
                                        .province(province)
                                        .district(district)
                                        .detailAddress(request.getDetailAddress())
                                        .job(job)
                                        .build();

                        locations.add(location);
                }
                return locations;
        }

        private List<JobIndustry> createJobIndustriesFromRequest(List<Long> industryIds, Job job) {
                List<JobIndustry> jobIndustries = new ArrayList<>();
                for (Long industryId : industryIds) {
                        Industry industry = industryRepository.findById(industryId)
                                        .orElseThrow(() -> {
                                                String message = messageSource.getMessage("industry.notFound", null,
                                                                LocaleContextHolder.getLocale());
                                                return new ResourceNotFoundException(message);
                                        });

                        JobIndustry jobIndustry = JobIndustry.builder()
                                        .job(job)
                                        .industry(industry)
                                        .build();

                        jobIndustries.add(jobIndustry);
                }
                return jobIndustries;
        }

        private void checkAuthorJob(Job entity) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                boolean isEmployer = AppUtils.hasRole(authentication, "EMPLOYER");
                String email = ((UserDetails) authentication.getPrincipal()).getUsername();
                if (isEmployer && !entity.getAuthor().getEmail().equals(email)) {
                        throw new AccessDeniedException("Access is denied");
                }

        }

        private PageResponse<List<JobResponse>> toPageResponse(Page<Job> page) {
                List<JobResponse> items = page.getContent().stream()
                                .map(mapper::toDTO)
                                .collect(Collectors.toList());

                return PageResponse.<List<JobResponse>>builder()
                                .pageNumber(page.getNumber() + 1)
                                .pageSize(page.getSize())
                                .totalPages(page.getTotalPages())
                                .numberOfElements(page.getNumberOfElements())
                                .items(items)
                                .build();
        }

        private void evictCacheByPattern(String pattern) {
                redisUtils.evictCacheByPattern(pattern);
        }
}