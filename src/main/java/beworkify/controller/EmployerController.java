package beworkify.controller;

import beworkify.dto.request.EmployerRequest;
import beworkify.dto.request.EmployerWebsiteUpdateRequest;
import beworkify.dto.request.UpdatePasswordRequest;
import beworkify.dto.response.EmployerResponse;
import beworkify.dto.response.PageResponse;
import beworkify.dto.response.ResponseData;
import beworkify.enumeration.LevelCompanySize;
import beworkify.enumeration.UserRole;
import beworkify.service.EmployerService;
import beworkify.util.AppUtils;
import beworkify.util.ResponseBuilder;
import beworkify.validation.OnAdmin;
import beworkify.validation.OnCreate;
import beworkify.validation.ValidImageFile;
import beworkify.validation.ValueOfEnum;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/employers")
public class EmployerController {
        private final EmployerService employerService;
        private final MessageSource messageSource;

        @GetMapping
        public ResponseEntity<ResponseData<PageResponse<List<EmployerResponse>>>> getEmployers(
                        @RequestParam(defaultValue = "1") int pageNumber,
                        @RequestParam(defaultValue = "10") int pageSize,
                        @RequestParam(required = false) List<String> sorts,
                        @RequestParam(defaultValue = "") String keyword,
                        @ValueOfEnum(enumClass = LevelCompanySize.class, message = "{error.invalid.level.company.size.enum}", required = false) @RequestParam(required = false) String companySize,
                        @Min(value = 1, message = "{validation.province.invalid}") @RequestParam(required = false) Long provinceId) {
                LevelCompanySize levelCompanySize = null;
                if (StringUtils.isNotBlank(companySize)) {
                        levelCompanySize = LevelCompanySize.fromLabel(companySize);
                }
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                boolean isAdmin = AppUtils.hasRole(authentication, UserRole.ADMIN.getName());
                PageResponse<List<EmployerResponse>> response = employerService
                                .getEmployersWithPaginationAndKeywordAndSorts(pageNumber, pageSize, sorts, keyword,
                                                levelCompanySize, provinceId, isAdmin);

                String message = messageSource.getMessage("employer.get.many.successfully", null,
                                LocaleContextHolder.getLocale());
                return ResponseBuilder.withData(HttpStatus.OK, message, response);
        }

        @GetMapping("/{id}")
        public ResponseEntity<ResponseData<EmployerResponse>> getEmployerById(@PathVariable Long id) {
                EmployerResponse response = employerService.getEmployerById(id);
                String message = messageSource.getMessage("employer.get.one.successfully", null,
                                LocaleContextHolder.getLocale());
                return ResponseBuilder.withData(HttpStatus.OK, message, response);
        }

        @PreAuthorize("hasRole('EMPLOYER')")
        @GetMapping("/me")
        public ResponseEntity<ResponseData<EmployerResponse>> getProfile() {
                Long employerId = AppUtils.getEmployerIdFromSecurityContext();
                EmployerResponse response = employerService.getEmployerById(employerId);
                String message = messageSource.getMessage("employer.get.one.successfully", null,
                                LocaleContextHolder.getLocale());
                return ResponseBuilder.withData(HttpStatus.OK, message, response);
        }

        @PostMapping("/sign-up")
        public ResponseEntity<ResponseData<EmployerResponse>> signUpEmployer(
                        @RequestBody @Validated({ OnCreate.class, Default.class }) EmployerRequest request)
                        throws MessagingException, UnsupportedEncodingException {
                EmployerResponse response = employerService.signUpEmployer(request);
                String message = messageSource.getMessage("employer.sign.up.successfully", null,
                                LocaleContextHolder.getLocale());
                return ResponseBuilder.withData(HttpStatus.CREATED, message, response);
        }

        @PreAuthorize("hasRole('ADMIN')")
        @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<ResponseData<EmployerResponse>> createEmployer(
                        @RequestPart("employer") @Validated({ OnAdmin.class, Default.class,
                                        OnCreate.class }) EmployerRequest request,
                        @ValidImageFile(required = false, message = "image.file.invalid") @RequestPart(name = "avatar", required = false) MultipartFile avatar,
                        @ValidImageFile(required = false, message = "image.file.invalid") @RequestPart(name = "background", required = false) MultipartFile background) {
                EmployerResponse response = employerService.createEmployer(request, avatar, background);
                String message = messageSource.getMessage("employer.create.successfully", null,
                                LocaleContextHolder.getLocale());
                return ResponseBuilder.withData(HttpStatus.CREATED, message, response);
        }

        @PreAuthorize("hasRole('ADMIN')")
        @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<ResponseData<EmployerResponse>> updateEmployer(
                        @PathVariable @Min(value = 1, message = "validation.id.min") Long id,
                        @RequestPart("employer") @Validated({ OnAdmin.class, Default.class }) EmployerRequest request,
                        @ValidImageFile(required = false, message = "image.file.invalid") @RequestPart(name = "avatar", required = false) MultipartFile avatar,
                        @ValidImageFile(required = false, message = "image.file.invalid") @RequestPart(name = "background", required = false) MultipartFile background) {
                EmployerResponse response = employerService.updateEmployer(id, request, avatar, background);
                String message = messageSource.getMessage("employer.update.successfully", null,
                                LocaleContextHolder.getLocale());
                return ResponseBuilder.withData(HttpStatus.OK, message, response);
        }

        @PreAuthorize("hasRole('EMPLOYER')")
        @PutMapping(value = "/me")
        public ResponseEntity<ResponseData<EmployerResponse>> updateProfileEmployer(
                        @RequestBody @Validated(Default.class) EmployerRequest request) {
                Long employerId = AppUtils.getEmployerIdFromSecurityContext();
                EmployerResponse response = employerService.updateProfileEmployer(employerId, request);
                String message = messageSource.getMessage("employer.update.successfully", null,
                                LocaleContextHolder.getLocale());
                return ResponseBuilder.withData(HttpStatus.OK, message, response);
        }

        @PreAuthorize("hasRole('ADMIN')")
        @DeleteMapping("/{id}")
        public ResponseEntity<ResponseData<Void>> deleteEmployer(@PathVariable Long id) {
                employerService.deleteEmployer(id);
                String message = messageSource.getMessage("employer.delete.successfully", null,
                                LocaleContextHolder.getLocale());
                return ResponseBuilder.noData(HttpStatus.OK, message);
        }

        @PreAuthorize("hasRole('EMPLOYER')")
        @PatchMapping(value = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<ResponseData<EmployerResponse>> uploadAvatar(
                        @RequestPart("avatar") @ValidImageFile(required = false, message = "image.file.invalid") MultipartFile avatar) {
                Long employerId = AppUtils.getEmployerIdFromSecurityContext();
                EmployerResponse response = employerService.uploadAvatar(employerId, avatar);
                String message = messageSource.getMessage("employer.avatar.upload.success", null,
                                LocaleContextHolder.getLocale());
                return ResponseBuilder.withData(HttpStatus.OK, message, response);
        }

        @PreAuthorize("hasRole('EMPLOYER')")
        @PatchMapping(value = "/me/background", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<ResponseData<EmployerResponse>> uploadBackground(
                        @RequestPart("background") @ValidImageFile(required = false, message = "image.file.invalid") MultipartFile background) {
                Long employerId = AppUtils.getEmployerIdFromSecurityContext();
                EmployerResponse response = employerService.uploadBackground(employerId, background);
                String message = messageSource.getMessage("employer.background.upload.success", null,
                                LocaleContextHolder.getLocale());
                return ResponseBuilder.withData(HttpStatus.OK, message, response);
        }

        @PreAuthorize("hasRole('EMPLOYER')")
        @PatchMapping("/me/password")
        public ResponseEntity<ResponseData<Void>> changePassword(@Valid @RequestBody UpdatePasswordRequest request) {
                Long employerId = AppUtils.getEmployerIdFromSecurityContext();
                log.info("Request: Update password for employer ID = {}", employerId);
                employerService.updatePassword(employerId, request);
                log.info("Response: password updated");
                String message = messageSource.getMessage("user.password.update.success", null,
                                LocaleContextHolder.getLocale());
                return ResponseBuilder.noData(HttpStatus.OK, message);
        }

        @PreAuthorize("hasRole('EMPLOYER')")
        @PatchMapping("/me/website-urls")
        public ResponseEntity<ResponseData<EmployerResponse>> updateWebsiteUrls(
                        @RequestBody EmployerWebsiteUpdateRequest request) {
                Long employerId = AppUtils.getEmployerIdFromSecurityContext();
                EmployerResponse response = employerService.updateWebsiteUrls(employerId, request);
                String message = messageSource.getMessage("employer.update.website.success", null,
                                LocaleContextHolder.getLocale());
                return ResponseBuilder.withData(HttpStatus.OK, message, response);
        }

}
