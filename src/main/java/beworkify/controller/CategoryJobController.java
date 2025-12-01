package beworkify.controller;

import beworkify.dto.request.CategoryJobRequest;
import beworkify.dto.response.CategoryJobResponse;
import beworkify.dto.response.PageResponse;
import beworkify.dto.response.ResponseData;
import beworkify.service.CategoryJobService;
import beworkify.util.ResponseBuilder;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing job categories. Provides endpoints for CRUD operations on job
 * categories.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/categories-job")
public class CategoryJobController {

  private final CategoryJobService service;
  private final MessageSource messageSource;

  @GetMapping("/all")
  public ResponseEntity<ResponseData<List<CategoryJobResponse>>> getAll() {
    List<CategoryJobResponse> response = service.getAll();
    String message =
        messageSource.getMessage(
            "jobCategory.get.list.success", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.withData(HttpStatus.OK, message, response);
  }

  @GetMapping("/industries/job-count")
  public ResponseEntity<ResponseData<List<CategoryJobResponse>>>
      getCategoriesJobWithCountJobIndustry() {
    List<CategoryJobResponse> response = service.getCategoriesJobWithCountJobIndustry();
    String message =
        messageSource.getMessage(
            "jobCategory.get.list.success", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.withData(HttpStatus.OK, message, response);
  }

  @GetMapping
  public ResponseEntity<ResponseData<PageResponse<List<CategoryJobResponse>>>> getAllWithPagination(
      @RequestParam(defaultValue = "1") @Min(value = 1, message = "{validation.page.number.min}")
          int pageNumber,
      @RequestParam(defaultValue = "10") @Min(value = 1, message = "{validation.page.size.min}")
          int pageSize,
      @RequestParam(required = false) List<String> sorts,
      @RequestParam(defaultValue = "") String keyword) {
    PageResponse<List<CategoryJobResponse>> response =
        service.getAllWithPaginationAndSort(pageNumber, pageSize, sorts, keyword);
    String message =
        messageSource.getMessage(
            "jobCategory.get.list.success", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.withData(HttpStatus.OK, message, response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ResponseData<CategoryJobResponse>> getById(
      @PathVariable("id") @Min(value = 1, message = "{validation.id.min}") Long id) {
    CategoryJobResponse dto = service.getById(id);
    String message =
        messageSource.getMessage("jobCategory.get.success", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.withData(HttpStatus.OK, message, dto);
  }

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ResponseData<CategoryJobResponse>> create(
      @Valid @RequestBody CategoryJobRequest request) {
    CategoryJobResponse dto = service.create(request);
    String message =
        messageSource.getMessage(
            "jobCategory.create.success", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.withData(HttpStatus.CREATED, message, dto);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ResponseData<CategoryJobResponse>> update(
      @PathVariable("id") @Min(value = 1, message = "{validation.id.min}") Long id,
      @Valid @RequestBody CategoryJobRequest request) {
    CategoryJobResponse dto = service.update(id, request);
    String message =
        messageSource.getMessage(
            "jobCategory.update.success", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.withData(HttpStatus.OK, message, dto);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ResponseData<Void>> delete(
      @PathVariable("id") @Min(value = 1, message = "{validation.id.min}") Long id) {
    service.delete(id);
    String message =
        messageSource.getMessage(
            "jobCategory.delete.success", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.noData(HttpStatus.OK, message);
  }
}
