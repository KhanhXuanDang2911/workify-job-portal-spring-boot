package beworkify.controller;

import beworkify.dto.response.JobResponse;
import beworkify.dto.response.PageResponse;
import beworkify.dto.response.ResponseData;
import beworkify.service.SavedJobService;
import beworkify.util.ResponseBuilder;
import jakarta.validation.constraints.Min;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/saved-jobs")
public class SavedJobController {

  private final SavedJobService service;
  private final MessageSource messageSource;

  @PostMapping("/toggle/{jobId}")
  @PreAuthorize("hasRole('JOB_SEEKER') or hasRole('ADMIN')")
  public ResponseEntity<ResponseData<Void>> toggle(
      @PathVariable @Min(value = 1, message = "{validation.id.min}") Long jobId) {
    log.info("Request: Toggle saved jobId={}", jobId);
    service.toggle(jobId);
    String message =
        messageSource.getMessage("saved_job.toggle.success", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.noData(HttpStatus.OK, message);
  }

  @GetMapping
  @PreAuthorize("hasRole('JOB_SEEKER') or hasRole('ADMIN')")
  public ResponseEntity<ResponseData<PageResponse<List<JobResponse>>>> getSavedJobs(
      @RequestParam(defaultValue = "1") @Min(value = 1, message = "{validation.page.number.min}")
          int pageNumber,
      @RequestParam(defaultValue = "10") @Min(value = 1, message = "{validation.page.size.min}")
          int pageSize) {
    log.info("Request: Get saved jobs with pageNumber={}, pageSize={}", pageNumber, pageSize);
    PageResponse<List<JobResponse>> response = service.getSavedJobs(pageNumber, pageSize);
    String message =
        messageSource.getMessage(
            "saved_job.get.list.success", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.withData(HttpStatus.OK, message, response);
  }

  @GetMapping("/check/{jobId}")
  @PreAuthorize("hasRole('JOB_SEEKER') or hasRole('ADMIN')")
  public ResponseEntity<ResponseData<Boolean>> checkSaved(
      @PathVariable @Min(value = 1, message = "{validation.id.min}") Long jobId) {
    log.info("Request: Check saved jobId={}", jobId);
    boolean saved = service.isSaved(jobId);
    String message =
        messageSource.getMessage("saved_job.check.success", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.withData(HttpStatus.OK, message, saved);
  }
}
