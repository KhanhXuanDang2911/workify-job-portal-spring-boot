
package beworkify.controller;

import beworkify.dto.request.ApplicationRequest;
import beworkify.dto.response.ApplicationResponse;
import beworkify.dto.response.PageResponse;
import beworkify.dto.response.ResponseData;
import beworkify.enumeration.ApplicationStatus;
import beworkify.enumeration.ErrorCode;
import beworkify.exception.AppException;
import beworkify.service.ApplicationService;
import beworkify.util.AppUtils;
import beworkify.util.ResponseBuilder;
import beworkify.validation.annotation.ValidDocFile;
import beworkify.validation.annotation.ValueOfEnum;
import beworkify.validation.group.OnLinkApply;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.*;
import jakarta.validation.constraints.Min;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/applications")
public class ApplicationController {
	private final ApplicationService service;
	private final MessageSource messageSource;
	private final Validator validator;

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasRole('JOB_SEEKER') or hasRole('ADMIN')")
	public ResponseEntity<ResponseData<ApplicationResponse>> apply(
			@Valid @RequestPart("application") ApplicationRequest request,
			@RequestPart("cv") @ValidDocFile(message = "{application.cv.file.not.valid}") MultipartFile cv) {
		log.info("Request: Create application for job id ={}", request.getJobId());
		ApplicationResponse dto = service.create(request, cv);
		String message = messageSource.getMessage("application.apply.success", null, LocaleContextHolder.getLocale());
		return ResponseBuilder.withData(HttpStatus.CREATED, message, dto);
	}

	@PostMapping(value = "/mobile", consumes = "multipart/form-data")
	@PreAuthorize("hasRole('JOB_SEEKER') or hasRole('ADMIN')")
	public ResponseEntity<ResponseData<ApplicationResponse>> mobileApply(
			@RequestHeader(value = "User-Agent") String userAgent, @RequestPart("application") String applicationJson,
			@RequestPart("cv") @ValidDocFile(message = "{application.cv.file.not.valid}") MultipartFile cv) {

		if (!AppUtils.isMobile(userAgent)) {
			throw new AppException(ErrorCode.ERROR_USER_AGENT_MOBILE_REQUIRED);
		}

		ApplicationRequest request;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			request = objectMapper.readValue(applicationJson, ApplicationRequest.class);
		} catch (JsonProcessingException ex) {
			throw new HttpMessageNotReadableException("Invalid JSON format: " + ex.getOriginalMessage(), ex);
		}

		Set<ConstraintViolation<ApplicationRequest>> violations = validator.validate(request);

		if (!violations.isEmpty()) {
			throw new ConstraintViolationException(violations);
		}

		ApplicationResponse dto = service.create(request, cv);
		String message = messageSource.getMessage("application.apply.success", null, LocaleContextHolder.getLocale());
		return ResponseBuilder.withData(HttpStatus.CREATED, message, dto);
	}

	@PostMapping(value = "/link", consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('JOB_SEEKER') or hasRole('ADMIN')")
	public ResponseEntity<ResponseData<ApplicationResponse>> applyWithoutFile(
			@RequestBody @Validated(OnLinkApply.class) ApplicationRequest request) {
		log.info("Request: Create application (link) for job id ={}", request.getJobId());
		ApplicationResponse dto = service.createWithoutFile(request);
		String message = messageSource.getMessage("application.apply.success", null, LocaleContextHolder.getLocale());
		return ResponseBuilder.withData(HttpStatus.CREATED, message, dto);
	}

	@GetMapping("/me")
	@PreAuthorize("hasRole('JOB_SEEKER') or hasRole('ADMIN')")
	public ResponseEntity<ResponseData<PageResponse<List<ApplicationResponse>>>> getMyApplications(
			@RequestParam(defaultValue = "1") @Min(value = 1, message = "{validation.page.number.min}") int pageNumber,
			@RequestParam(defaultValue = "10") @Min(value = 1, message = "{validation.page.size.min}") int pageSize,
			@RequestParam(required = false) List<String> sorts) {
		log.info("Request: Get my applications pageNumber={}, pageSize={}", pageNumber, pageSize);
		PageResponse<List<ApplicationResponse>> response = service.getMyApplications(pageNumber, pageSize, sorts);
		String message = messageSource.getMessage("application.get.list.success", null,
				LocaleContextHolder.getLocale());
		return ResponseBuilder.withData(HttpStatus.OK, message, response);
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('JOB_SEEKER', 'ADMIN', 'EMPLOYER')")
	public ResponseEntity<ResponseData<ApplicationResponse>> getById(
			@PathVariable("id") @Min(value = 1, message = "{validation.id.min}") Long id) {
		log.info("Request: Get application by id = {}", id);
		ApplicationResponse dto = service.getById(id);
		String message = messageSource.getMessage("application.get.success", null, LocaleContextHolder.getLocale());
		return ResponseBuilder.withData(HttpStatus.OK, message, dto);
	}

	@GetMapping("/latest/{jobId}")
	@PreAuthorize("hasRole('JOB_SEEKER') or hasRole('ADMIN')")
	public ResponseEntity<ResponseData<ApplicationResponse>> getLatestByJob(
			@PathVariable("jobId") @Min(value = 1, message = "{validation.id.min}") Long jobId) {
		log.info("Request: Get latest application by job id = {}", jobId);
		ApplicationResponse dto = service.getLatestByJob(jobId);
		String message = messageSource.getMessage("application.get.success", null, LocaleContextHolder.getLocale());
		return ResponseBuilder.withData(HttpStatus.OK, message, dto);
	}

	@GetMapping("/job/{jobId}")
	@PreAuthorize("hasRole('EMPLOYER')")
	public ResponseEntity<ResponseData<PageResponse<List<ApplicationResponse>>>> getByJobId(
			@PathVariable("jobId") @Min(value = 1, message = "{validation.id.min}") Long jobId,
			@RequestParam(defaultValue = "1") @Min(value = 1, message = "{validation.page.number.min}") int pageNumber,
			@RequestParam(defaultValue = "10") @Min(value = 1, message = "{validation.page.size.min}") int pageSize,
			@RequestParam(required = false) @Min(value = 1, message = "{validation.day.min}") Integer receivedWithin,
			@RequestParam(required = false) @ValueOfEnum(enumClass = ApplicationStatus.class, required = false, message = "{error.invalid.application.status.enum}") String status) {
		PageResponse<List<ApplicationResponse>> dto = service.getApplicationsByJobId(pageNumber, pageSize, jobId,
				receivedWithin, status != null ? ApplicationStatus.fromValue(status) : null);
		String message = messageSource.getMessage("application.get.success", null, LocaleContextHolder.getLocale());
		return ResponseBuilder.withData(HttpStatus.OK, message, dto);
	}

	@DeleteMapping(value = "/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ResponseData<Void>> delete(
			@PathVariable("id") @Min(value = 1, message = "{validation.id.min}") Long id) {
		log.info("Request: Delete application id = {}", id);
		service.deleteById(id);
		String message = messageSource.getMessage("application.delete.success", null, LocaleContextHolder.getLocale());
		return ResponseBuilder.noData(HttpStatus.OK, message);
	}

	@PatchMapping(value = "/{id}/status")
	@PreAuthorize("hasRole('EMPLOYER')")
	public ResponseEntity<ResponseData<ApplicationResponse>> changeStatus(
			@PathVariable("id") @Min(value = 1, message = "{validation.id.min}") Long id,
			@RequestParam("status") @ValueOfEnum(enumClass = ApplicationStatus.class, message = "{error.invalid.application.status.enum}") String status) {
		log.info("Request: Change status application id = {} to {}", id, status);
		ApplicationResponse dto = service.changeStatus(id, ApplicationStatus.fromValue(status));
		String message = messageSource.getMessage("application.update.status.success", null,
				LocaleContextHolder.getLocale());
		return ResponseBuilder.withData(HttpStatus.OK, message, dto);
	}
}
