package beworkify.service.impl;

import beworkify.dto.request.*;
import beworkify.dto.response.EmployerResponse;
import beworkify.dto.response.PageResponse;
import beworkify.entity.District;
import beworkify.entity.Employer;
import beworkify.entity.Province;
import beworkify.enumeration.ErrorCode;
import beworkify.enumeration.TokenType;
import beworkify.exception.AppException;
import beworkify.exception.InvalidTokenException;
import beworkify.exception.ResourceNotFoundException;
import beworkify.mapper.EmployerMapper;
import beworkify.service.*;
import beworkify.service.redis.RedisOTPCodeService;
import beworkify.service.redis.RedisTokenService;
import beworkify.util.RedisUtils;
import jakarta.mail.MessagingException;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;
import beworkify.repository.EmployerRepository;
import beworkify.exception.ResourceConflictException;
import beworkify.enumeration.LevelCompanySize;
import beworkify.enumeration.StatusUser;
import org.springframework.data.domain.Page;
import beworkify.util.AppUtils;
import org.springframework.context.MessageSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EmployerServiceImpl implements EmployerService {

    private final EmployerRepository employerRepository;
    private final EmployerMapper employerMapper;
    private final MessageSource messageSource;
    private final AzureBlobService azureBlobService;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final JwtService jwtService;
    private final ProvinceService provinceService;
    private final DistrictService districtService;
    private final RedisTokenService redisTokenService;
    private final RedisOTPCodeService redisOTPCodeService;
    private final RedisUtils redisUtils;
    private final beworkify.repository.JobRepository jobRepository;

    @Override
    public PageResponse<List<EmployerResponse>> getEmployersWithPaginationAndKeywordAndSorts(int pageNumber,
            int pageSize,
            List<String> sorts, String keyword, LevelCompanySize companySize, Long provinceId, boolean isAdmin) {
        List<String> whiteListFieldSorts = List.of("companyName", "companySize", "status", "email", "createdAt",
                "updatedAt", "province.name", "district.name");
        Pageable pageable = AppUtils.generatePageableWithSort(sorts, whiteListFieldSorts, pageNumber, pageSize);
        Page<Employer> page = employerRepository.searchEmployers(keyword.toLowerCase(), companySize, provinceId,
                isAdmin,
                pageable);

        List<Employer> employers = page.getContent();
        List<Long> employerIds = employers.stream().map(Employer::getId).toList();
        Map<Long, Long> hiringCountMap = employerIds.isEmpty() ? Map.of()
                : jobRepository.countHiringJobsByEmployerIds(employerIds).stream()
                        .collect(Collectors.toMap(r -> (Long) r[0], r -> (Long) r[1]));

        List<EmployerResponse> items = employers.stream().map(e -> {
            EmployerResponse dto = employerMapper.toDTO(e);
            int count = hiringCountMap.getOrDefault(e.getId(), 0L).intValue();
            dto.setNumberOfHiringJobs(count);
            return dto;
        }).toList();
        return PageResponse.<List<EmployerResponse>>builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .totalPages(page.getTotalPages())
                .numberOfElements(page.getNumberOfElements())
                .items(items)
                .build();
    }

    @Override
    @CachePut(value = "employers", key = "#result.id", condition = "#result != null")
    public EmployerResponse signUpEmployer(EmployerRequest request, boolean isMobile)
            throws MessagingException, UnsupportedEncodingException {
        existsByEmail(request.getEmail());
        Province province = provinceService.findProvinceById(request.getProvinceId());
        District district = districtService.findDistrictById(request.getDistrictId());

        Employer employer = Employer.builder()
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .companyName(request.getCompanyName())
                .companySize(LevelCompanySize.fromLabel(request.getCompanySize()))
                .contactPerson(request.getContactPerson())
                .detailAddress(request.getDetailAddress())
                .status(StatusUser.PENDING)
                .build();
        employer.setProvince(province);
        employer.setDistrict(district);
        employer.setEmployerSlug(AppUtils.toSlug(employer.getCompanyName()));
        employerRepository.save(employer);
        if (isMobile) {
            mailService.sendConfirmLink(employer, true,
                    redisOTPCodeService.generateAndSaveOTPCode(request.getEmail(), (long) 24 * 60));
        } else {
            mailService.sendConfirmLink(employer, false, null);
        }
        EmployerResponse response = employerMapper.toDTO(employer);
        response.setNumberOfHiringJobs(0);
        return response;
    }

    @Override
    public void verifyEmailEmployer(String confirmToken) {
        String email = jwtService.extractEmail(confirmToken, TokenType.CONFIRM_TOKEN);
        Employer employer = findEmployerByEmail(email);
        if (!employer.getStatus().equals(StatusUser.PENDING)
                || !jwtService.isTokenValid(confirmToken, employer, TokenType.CONFIRM_TOKEN)) {
            throw new AppException(ErrorCode.VERIFY_EMAIL_FAILED);
        }
        employer.setStatus(StatusUser.ACTIVE);
        employerRepository.save(employer);
        evictCacheByPattern("employers:" + employer.getId());
    }

    @Override
    public void verifyEmailEmployerMobile(VerifyEmailMobileRequest request) {
        Employer employer = findEmployerByEmail(request.getEmail());
        if (!employer.getStatus().equals(StatusUser.PENDING)
                || !redisOTPCodeService.isValidCode(request.getCode(), request.getEmail())) {
            throw new AppException(ErrorCode.VERIFY_EMAIL_FAILED);
        }
        employer.setStatus(StatusUser.ACTIVE);
        redisOTPCodeService.deleteOTPCode(request.getCode());
        employerRepository.save(employer);
        evictCacheByPattern("employers:" + employer.getId());
    }

    @PostAuthorize("hasRole('ADMIN') or returnObject.status == T(beworkify.enumeration.StatusUser).ACTIVE")
    @Override
    @Cacheable(value = "employers", key = "#id")
    public EmployerResponse getEmployerById(Long id) {
        Employer employer = findEmployerById(id);
        EmployerResponse dto = employerMapper.toDTO(employer);
        long hiringCount = jobRepository.countHiringJobsByEmployerId(employer.getId());
        dto.setNumberOfHiringJobs((int) hiringCount);
        return dto;
    }

    @Override

    @CachePut(value = "employers", key = "#result.id", condition = "#result != null")
    public EmployerResponse createEmployer(EmployerRequest request, MultipartFile avatar, MultipartFile background) {
        existsByEmail(request.getEmail());
        Province province = provinceService.findProvinceById(request.getProvinceId());
        District district = districtService.findDistrictById(request.getDistrictId());
        Employer employer = employerMapper.toEntity(request);
        employer.setStatus(StatusUser.getStatusFromName(request.getStatus()));
        employer.setCompanySize(LevelCompanySize.fromLabel(request.getCompanySize()));
        employer.setPassword(passwordEncoder.encode(request.getPassword()));
        employer.setProvince(province);
        employer.setDistrict(district);
        employer.setEmployerSlug(AppUtils.toSlug(employer.getCompanyName()));
        if (avatar != null && !avatar.isEmpty()) {
            String avatarUrl = azureBlobService.uploadFile(avatar);
            employer.setAvatarUrl(avatarUrl);
        }
        if (background != null && !background.isEmpty()) {
            String backgroundUrl = azureBlobService.uploadFile(background);
            employer.setBackgroundUrl(backgroundUrl);
        }
        employerRepository.save(employer);
        EmployerResponse dto = employerMapper.toDTO(employer);
        dto.setNumberOfHiringJobs(0);
        return dto;
    }

    @Override
    @CachePut(value = "employers", key = "#result.id", condition = "#result != null")
    public EmployerResponse updateEmployer(Long id, EmployerRequest request, MultipartFile avatar,
            MultipartFile background) {
        Employer employer = findEmployerById(id);
        if (request.getEmail() != null && employerRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
            String message = messageSource.getMessage("employer.email.exists", new Object[] { request.getEmail() },
                    LocaleContextHolder.getLocale());
            throw new ResourceConflictException(message);
        }
        Province province = provinceService.findProvinceById(request.getProvinceId());
        District district = districtService.findDistrictById(request.getDistrictId());
        employerMapper.updateEntityFromDTO(request, employer);
        employer.setStatus(StatusUser.getStatusFromName(request.getStatus()));
        employer.setCompanySize(LevelCompanySize.fromLabel(request.getCompanySize()));
        employer.setProvince(province);
        employer.setDistrict(district);
        employer.setEmployerSlug(AppUtils.toSlug(employer.getCompanyName()));
        employer.setEmail(request.getEmail());
        if (request.getPassword() != null) {
            employer.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (avatar != null && !avatar.isEmpty()) {
            String avatarUrl = azureBlobService.uploadFile(avatar);
            employer.setAvatarUrl(avatarUrl);
        }
        if (background != null && !background.isEmpty()) {
            String backgroundUrl = azureBlobService.uploadFile(background);
            employer.setBackgroundUrl(backgroundUrl);
        }
        employerRepository.save(employer);
        EmployerResponse dto = employerMapper.toDTO(employer);
        long hiringCount = jobRepository.countHiringJobsByEmployerId(employer.getId());
        dto.setNumberOfHiringJobs((int) hiringCount);
        return dto;
    }

    @Override
    @CachePut(value = "employers", key = "#result.id", condition = "#result != null")
    public EmployerResponse updateProfileEmployer(Long id, EmployerRequest request) {
        Employer employer = findEmployerById(id);
        Province province = provinceService.findProvinceById(request.getProvinceId());
        District district = districtService.findDistrictById(request.getDistrictId());
        employerMapper.updateEntityFromDTO(request, employer);
        employer.setCompanySize(LevelCompanySize.fromLabel(request.getCompanySize()));
        employer.setProvince(province);
        employer.setDistrict(district);
        employer.setEmployerSlug(AppUtils.toSlug(employer.getCompanyName()));
        employerRepository.save(employer);
        EmployerResponse dto = employerMapper.toDTO(employer);
        long hiringCount = jobRepository.countHiringJobsByEmployerId(employer.getId());
        dto.setNumberOfHiringJobs((int) hiringCount);
        return dto;
    }

    @Override
    @CachePut(value = "employers", key = "#result.id", condition = "#result != null")
    public EmployerResponse uploadAvatar(Long id, MultipartFile avatar) {
        Employer employer = findEmployerById(id);
        if (avatar != null && !avatar.isEmpty()) {
            String avatarUrl = azureBlobService.uploadFile(avatar);
            employer.setAvatarUrl(avatarUrl);
            employerRepository.save(employer);
        }
        EmployerResponse dto = employerMapper.toDTO(employer);
        long hiringCount = jobRepository.countHiringJobsByEmployerId(employer.getId());
        dto.setNumberOfHiringJobs((int) hiringCount);
        return dto;
    }

    @Override
    @CachePut(value = "employers", key = "#result.id", condition = "#result != null")
    public EmployerResponse uploadBackground(Long id, MultipartFile background) {
        Employer employer = findEmployerById(id);
        if (background != null && !background.isEmpty()) {
            String backgroundUrl = azureBlobService.uploadFile(background);
            employer.setBackgroundUrl(backgroundUrl);
            employerRepository.save(employer);
        }
        EmployerResponse dto = employerMapper.toDTO(employer);
        long hiringCount = jobRepository.countHiringJobsByEmployerId(employer.getId());
        dto.setNumberOfHiringJobs((int) hiringCount);
        return dto;
    }

    @Override
    @CacheEvict(value = "employers", key = "#id")
    public void deleteEmployer(Long id) {
        Employer employer = employerRepository.findById(id).orElseThrow(() -> {
            String message = messageSource.getMessage("employer.not.found", new Object[] { id },
                    LocaleContextHolder.getLocale());
            return new ResourceNotFoundException(message);
        });
        employerRepository.delete(employer);
    }

    @Override
    public Employer findEmployerByEmail(String email) {
        return employerRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Employer have email {} not found", email);
                    String message = messageSource.getMessage("employer.not.found.by.email", new Object[] { email },
                            LocaleContextHolder.getLocale());
                    return new ResourceNotFoundException(message);
                });
    }

    private Employer findEmployerById(Long id) {
        return employerRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Employer have id {} not found", id);
                    String message = messageSource.getMessage("employer.not.found", null,
                            LocaleContextHolder.getLocale());
                    return new ResourceNotFoundException(message);
                });
    }

    private void existsByEmail(String email) {
        if (employerRepository.existsByEmail(email)) {
            String message = messageSource.getMessage("employer.email.exists", new Object[] { email },
                    LocaleContextHolder.getLocale());
            throw new ResourceConflictException(message);
        }
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request, boolean isMobile)
            throws MessagingException, UnsupportedEncodingException {
        Employer employer = findEmployerByEmail(request.getEmail());
        if (!employer.getStatus().equals(StatusUser.ACTIVE)) {
            throw new AppException(ErrorCode.ACCOUNT_NOT_ACTIVE);
        }
        if (isMobile) {
            mailService.sendResetLink(employer, true,
                    redisOTPCodeService.generateAndSaveOTPCode(request.getEmail(), 60));
        } else {
            mailService.sendResetLink(employer, false, null);
        }
    }

    @Override
    public void resetPassword(String token, ResetPasswordRequest request) {
        String email = jwtService.extractEmail(token, TokenType.RESET_TOKEN);
        Employer employer = findEmployerByEmail(email);
        if (!jwtService.isTokenValid(token, employer, TokenType.RESET_TOKEN)
                || !redisTokenService.existsByJwtId(token, TokenType.RESET_TOKEN)) {
            throw new InvalidTokenException();
        }
        employer.setPassword(passwordEncoder.encode(request.getNewPassword()));
        redisTokenService.deleteByJwtId(token, TokenType.RESET_TOKEN);
        employerRepository.save(employer);
    }

    @Override
    public void resetPasswordEmployerMobile(ResetPasswordMobileRequest request) {
        Employer employer = findEmployerByEmail(request.getEmail());
        if (!redisOTPCodeService.isValidCode(request.getCode(), request.getEmail())) {
            throw new AppException(ErrorCode.RESET_PASSWORD_FAILED);
        }
        employer.setPassword(passwordEncoder.encode(request.getNewPassword()));
        redisOTPCodeService.deleteOTPCode(request.getCode());
        employerRepository.save(employer);
    }

    @Override
    public void updatePassword(Long id, UpdatePasswordRequest request) {
        Employer employer = findEmployerById(id);
        if (!passwordEncoder.matches(request.getCurrentPassword(), employer.getPassword())) {
            throw new AppException(ErrorCode.PASSWORD_MISMATCH);
        }
        employer.setPassword(passwordEncoder.encode(request.getNewPassword()));
        employerRepository.save(employer);
    }

    @Override
    @CachePut(value = "employers", key = "#result.id", condition = "#result != null")
    public EmployerResponse updateWebsiteUrls(Long employerId, EmployerWebsiteUpdateRequest request) {
        Employer employer = findEmployerById(employerId);
        employer.setWebsiteUrls(request.getWebsiteUrls());
        employer.setFacebookUrl(request.getFacebookUrl());
        employer.setTwitterUrl(request.getTwitterUrl());
        employer.setLinkedinUrl(request.getLinkedinUrl());
        employer.setGoogleUrl(request.getGoogleUrl());
        employer.setYoutubeUrl(request.getYoutubeUrl());
        employerRepository.save(employer);
        EmployerResponse dto = employerMapper.toDTO(employer);
        long hiringCount = jobRepository.countHiringJobsByEmployerId(employer.getId());
        dto.setNumberOfHiringJobs((int) hiringCount);
        return dto;
    }

    private void evictCacheByPattern(String pattern) {
        redisUtils.evictCacheByPattern(pattern);
    }

    @Override
    @Cacheable(value = "employers_top_hiring", key = "'limit:' + #limit")
    public List<EmployerResponse> getTopHiringEmployers(int limit) {
        var rows = jobRepository.findTopEmployerIdsByHiringJobs(PageRequest.of(0, Math.max(1, limit)));
        List<Long> orderedIds = rows.stream().map(r -> (Long) r[0]).toList();
        var countMap = rows.stream().collect(Collectors.toMap(r -> (Long) r[0], r -> (Long) r[1]));
        if (orderedIds.isEmpty())
            return List.of();
        var employers = employerRepository.findAllById(orderedIds);
        var byId = employers.stream().collect(Collectors.toMap(Employer::getId, e -> e));
        return orderedIds.stream()
                .map(id -> byId.get(id))
                .filter(e -> e != null)
                .map(e -> {
                    EmployerResponse dto = employerMapper.toDTO(e);
                    dto.setNumberOfHiringJobs(countMap.getOrDefault(e.getId(), 0L).intValue());
                    return dto;
                }).toList();
    }
}
