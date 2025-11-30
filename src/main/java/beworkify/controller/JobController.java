package beworkify.controller;

import beworkify.dto.request.JobRequest;
import beworkify.dto.response.*;
import beworkify.enumeration.*;
import beworkify.search.service.JobSearchService;
import beworkify.service.JobService;
import beworkify.util.AppUtils;
import beworkify.util.ResponseBuilder;
import beworkify.validation.annotation.ValueOfEnum;
import beworkify.validation.annotation.ValueOfEnumList;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/jobs")
public class JobController {

  private final JobService service;
  private final JobSearchService jobSearchService;
  private final MessageSource messageSource;

  @GetMapping("/advanced")
  public ResponseEntity<ResponseData<PageResponse<List<JobResponse>>>> searchJobsAdvanced(
      @RequestParam(defaultValue = "") String keyword,
      @RequestParam(required = false) List<String> industryIds,
      @RequestParam(required = false) List<String> provinceIds,
      @RequestParam(required = false)
          @ValueOfEnumList(enumClass = JobLevel.class, message = "{error.invalid.job.level.enum}")
          List<String> jobLevels,
      @RequestParam(required = false)
          @ValueOfEnumList(enumClass = JobType.class, message = "{error.invalid.job.type.enum}")
          List<String> jobTypes,
      @RequestParam(required = false)
          @ValueOfEnumList(
              enumClass = ExperienceLevel.class,
              message = "{error.invalid.experience.level.enum}")
          List<String> experienceLevels,
      @RequestParam(required = false)
          @ValueOfEnumList(
              enumClass = EducationLevel.class,
              message = "{error.invalid.education.level.enum}")
          List<String> educationLevels,
      @RequestParam(required = false) @Min(value = 1, message = "{validation.day.min}")
          Integer postedWithinDays,
      @RequestParam(required = false)
          @DecimalMin(value = "0.0", message = "{validation.job.minSalary.min}")
          Double minSalary,
      @RequestParam(required = false)
          @DecimalMin(value = "0.0", message = "{validation.job.maxSalary.min}")
          Double maxSalary,
      @RequestParam(required = false)
          @ValueOfEnum(
              enumClass = SalaryUnit.class,
              message = "{error.invalid.salary.unit.enum}",
              required = false)
          String salaryUnit,
      @RequestParam(required = false) String sort,
      @RequestParam(defaultValue = "1") @Min(value = 1, message = "{validation.page.number.min}")
          int pageNumber,
      @RequestParam(defaultValue = "10") @Min(value = 1, message = "{validation.page.size.min}")
          int pageSize) {

    log.info("Jobs search advanced");

    Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);

    var jobsPage =
        jobSearchService.searchAdvanced(
            keyword,
            industryIds,
            provinceIds,
            jobLevels,
            jobTypes,
            experienceLevels,
            educationLevels,
            postedWithinDays,
            minSalary,
            maxSalary,
            salaryUnit,
            sort,
            pageable);

    String message =
        messageSource.getMessage("job.get.list.success", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.withData(HttpStatus.OK, message, jobsPage);
  }

  @GetMapping("/all")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ResponseData<PageResponse<List<JobResponse>>>> getAll(
      @RequestParam(defaultValue = "1") @Min(value = 1, message = "{validation.page.number.min}")
          int pageNumber,
      @RequestParam(defaultValue = "10") @Min(value = 1, message = "{validation.page.size.min}")
          int pageSize,
      @RequestParam(required = false) @Min(value = 1, message = "{validation.id.min}")
          Long industryId,
      @RequestParam(required = false) @Min(value = 1, message = "{validation.id.min}")
          Long provinceId,
      @RequestParam(required = false) List<String> sorts,
      @RequestParam(defaultValue = "") String keyword) {

    log.info(
        "Request: Get all jobs with pageNumber={}, pageSize={}, sorts={}, keyword={}",
        pageNumber,
        pageSize,
        sorts,
        keyword);
    PageResponse<List<JobResponse>> response =
        service.getAllJobs(pageNumber, pageSize, industryId, provinceId, sorts, keyword);
    String message =
        messageSource.getMessage("job.get.list.success", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.withData(HttpStatus.OK, message, response);
  }

  @GetMapping("/me")
  @PreAuthorize("hasRole('EMPLOYER')")
  public ResponseEntity<ResponseData<PageResponse<List<JobResponse>>>> getMyJobs(
      @RequestParam(defaultValue = "1") @Min(value = 1, message = "{validation.page.number.min}")
          int pageNumber,
      @RequestParam(defaultValue = "10") @Min(value = 1, message = "{validation.page.size.min}")
          int pageSize,
      @RequestParam(required = false) @Min(value = 1, message = "{validation.id.min}")
          Long industryId,
      @RequestParam(required = false) @Min(value = 1, message = "{validation.id.min}")
          Long provinceId,
      @RequestParam(required = false) List<String> sorts,
      @RequestParam(defaultValue = "") String keyword) {

    log.info(
        "Request: Get my jobs with pageNumber={}, pageSize={}, sorts={}, keyword={}",
        pageNumber,
        pageSize,
        sorts,
        keyword);
    PageResponse<List<JobResponse>> response =
        service.getMyJobs(pageNumber, pageSize, industryId, provinceId, sorts, keyword);
    String message =
        messageSource.getMessage("job.get.list.success", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.withData(HttpStatus.OK, message, response);
  }

  @GetMapping("/openings/{employerId}")
  public ResponseEntity<ResponseData<PageResponse<List<JobResponse>>>> getHiringJobs(
      @RequestParam(defaultValue = "1") @Min(value = 1, message = "{validation.page.number.min}")
          int pageNumber,
      @RequestParam(defaultValue = "10") @Min(value = 1, message = "{validation.page.size.min}")
          int pageSize,
      @RequestParam(required = false) List<String> sorts,
      @PathVariable @Min(value = 1, message = "{validation.id.min}") Long employerId) {
    log.info(
        "Request: Get hiring jobs with pageNumber={}, pageSize={}, sorts={}",
        pageNumber,
        pageSize,
        sorts);
    PageResponse<List<JobResponse>> response =
        service.getHiringJobs(employerId, pageNumber, pageSize, sorts);
    String message =
        messageSource.getMessage(
            "job.get.hiring.job.success", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.withData(HttpStatus.OK, message, response);
  }

  @GetMapping("/me/industries/current")
  @PreAuthorize("hasRole('EMPLOYER')")
  public ResponseEntity<ResponseData<List<IndustryResponse>>> getMyCurrentIndustries() {

    log.info("Request: Get my current industries");
    Long employerId = AppUtils.getEmployerIdFromSecurityContext();
    List<IndustryResponse> response = service.getMyCurrentIndustries(employerId);
    String message =
        messageSource.getMessage(
            "industry.get.list.success", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.withData(HttpStatus.OK, message, response);
  }

  @GetMapping("/me/locations/current")
  @PreAuthorize("hasRole('EMPLOYER')")
  public ResponseEntity<ResponseData<List<ProvinceResponse>>> getMyCurrentLocations() {
    log.info("Request: Get my current locations");
    Long employerId = AppUtils.getEmployerIdFromSecurityContext();
    List<ProvinceResponse> response = service.getMyCurrentLocations(employerId);
    String message =
        messageSource.getMessage(
            "job.location.get.list.success", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.withData(HttpStatus.OK, message, response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ResponseData<JobResponse>> getById(
      @PathVariable("id") @Min(value = 1, message = "{validation.id.min}") Long id) {
    log.info("Request: Get job by id = {}", id);
    JobResponse dto = service.getById(id);
    String message =
        messageSource.getMessage("job.get.success", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.withData(HttpStatus.OK, message, dto);
  }

  @GetMapping("/locations/popular")
  public ResponseEntity<ResponseData<List<PopularLocationResponse>>> getPopularLocations(
      @RequestParam(defaultValue = "10") @Min(value = 1, message = "{validation.limit.min}")
          Integer limit) {
    log.info("Request: Get popular locations with limit = {}", limit);
    List<PopularLocationResponse> response = service.getPopularLocations(limit);
    String message =
        messageSource.getMessage(
            "job.location.get.popular.success", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.withData(HttpStatus.OK, message, response);
  }

  @GetMapping("/industries/popular")
  public ResponseEntity<ResponseData<List<PopularIndustryResponse>>> getPopularIndustries(
      @RequestParam(defaultValue = "10") @Min(value = 1, message = "{validation.limit.min}")
          Integer limit) {
    log.info("Request: Get popular industries with limit = {}", limit);
    List<PopularIndustryResponse> response = service.getPopularIndustries(limit);
    String message =
        messageSource.getMessage(
            "job.industries.get.popular.success", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.withData(HttpStatus.OK, message, response);
  }

  @GetMapping("/top-attractive")
  public ResponseEntity<ResponseData<List<JobResponse>>> getTopAttractive(
      @RequestParam(defaultValue = "10") @Min(value = 1, message = "{validation.limit.min}")
          Integer limit,
      @RequestParam(required = false) @Min(value = 1, message = "{validation.id.min}")
          Long industryId) {
    log.info("Request: Get top attractive jobs with limit = {}", limit);
    List<JobResponse> response = service.getTopAttractiveJobs(limit, industryId);
    String message =
        messageSource.getMessage(
            "job.get.top.attractive.success", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.withData(HttpStatus.OK, message, response);
  }

  @PostMapping
  @PreAuthorize("hasRole('EMPLOYER')")
  public ResponseEntity<ResponseData<JobResponse>> create(@Valid @RequestBody JobRequest request) {
    log.info("Request: Employer create job");
    JobResponse dto = service.create(request);
    String message =
        messageSource.getMessage("job.create.success", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.withData(HttpStatus.CREATED, message, dto);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('EMPLOYER')")
  public ResponseEntity<ResponseData<JobResponse>> update(
      @PathVariable("id") @Min(value = 1, message = "{validation.id.min}") Long id,
      @Valid @RequestBody JobRequest request) {
    log.info("Request: Employer update job with id = {}", id);
    JobResponse dto = service.update(id, request);
    String message =
        messageSource.getMessage("job.update.success", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.withData(HttpStatus.OK, message, dto);
  }

  @PatchMapping("/close/{id}")
  @PreAuthorize("hasRole('EMPLOYER')")
  public ResponseEntity<ResponseData<Void>> closeJob(
      @PathVariable("id") @Min(value = 1, message = "{validation.id.min}") Long id) {
    log.info("Request: Employer close job with id = {}", id);
    service.closeJob(id);
    String message =
        messageSource.getMessage("job.close.success", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.noData(HttpStatus.OK, message);
  }

  @PatchMapping("/status/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ResponseData<Void>> adminUpdateStatusJob(
      @PathVariable("id") @Min(value = 1, message = "{validation.id.min}") Long id,
      @ValueOfEnum(enumClass = JobStatus.class, message = "{error.invalid.job.status.enum}")
          @RequestParam
          String status) {
    log.info("Request: Admin update job with id = {}", id);
    service.updateStatus(id, JobStatus.fromValue(status));
    String message =
        messageSource.getMessage(
            "job.update.status.success", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.noData(HttpStatus.OK, message);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYER')")
  public ResponseEntity<ResponseData<Void>> delete(
      @PathVariable("id") @Min(value = 1, message = "{validation.id.min}") Long id) {
    log.info("Request: Delete job with id = {}", id);
    service.delete(id);
    String message =
        messageSource.getMessage("job.delete.success", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.noData(HttpStatus.OK, message);
  }

  @GetMapping("/personalized")
  public ResponseEntity<ResponseData<List<JobResponse>>> getPersonalized(
      @RequestParam(defaultValue = "10") @Min(value = 1, message = "{validation.limit.min}")
          Integer limit) {

    log.info("Request: Get personalized jobs with limit = {}", limit);
    List<JobResponse> response = service.getPersonalizedJobsForCaller(limit);

    String message =
        messageSource.getMessage(
            "job.get.personalized.success", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.withData(HttpStatus.OK, message, response);
  }
}
