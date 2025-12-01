package beworkify.controller;

import beworkify.dto.request.IndustryRequest;
import beworkify.dto.response.IndustryResponse;
import beworkify.dto.response.PageResponse;
import beworkify.dto.response.ResponseData;
import beworkify.service.IndustryService;
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
 * REST controller for managing industries. Provides endpoints for CRUD operations on industry data.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/industries")
public class IndustryController {

  private final IndustryService service;
  private final MessageSource messageSource;

  @GetMapping("/all")
  public ResponseEntity<ResponseData<List<IndustryResponse>>> getAll() {
    List<IndustryResponse> response = service.getAll();
    String message =
        messageSource.getMessage(
            "industry.get.list.success", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.withData(HttpStatus.OK, message, response);
  }

  @GetMapping
  public ResponseEntity<ResponseData<PageResponse<List<IndustryResponse>>>> getAllWithPagination(
      @RequestParam(defaultValue = "1") @Min(value = 1, message = "{validation.page.number.min}")
          int pageNumber,
      @RequestParam(defaultValue = "10") @Min(value = 1, message = "{validation.page.size.min}")
          int pageSize,
      @RequestParam(required = false) List<String> sorts,
      @RequestParam(defaultValue = "") String keyword,
      @RequestParam(required = false) @Min(value = 1, message = "{validation.id.min}")
          Long categoryId) {
    PageResponse<List<IndustryResponse>> response =
        service.getAllWithPaginationAndSort(pageNumber, pageSize, sorts, keyword, categoryId);
    String message =
        messageSource.getMessage(
            "industry.get.list.success", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.withData(HttpStatus.OK, message, response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ResponseData<IndustryResponse>> getById(
      @PathVariable("id") @Min(value = 1, message = "{validation.id.min}") Long id) {
    IndustryResponse dto = service.getById(id);
    String message =
        messageSource.getMessage("industry.get.success", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.withData(HttpStatus.OK, message, dto);
  }

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ResponseData<IndustryResponse>> create(
      @Valid @RequestBody IndustryRequest request) {
    IndustryResponse dto = service.create(request);
    String message =
        messageSource.getMessage("industry.create.success", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.withData(HttpStatus.CREATED, message, dto);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ResponseData<IndustryResponse>> update(
      @PathVariable("id") @Min(value = 1, message = "{validation.id.min}") Long id,
      @Valid @RequestBody IndustryRequest request) {
    IndustryResponse dto = service.update(id, request);
    String message =
        messageSource.getMessage("industry.update.success", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.withData(HttpStatus.OK, message, dto);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ResponseData<Void>> delete(
      @PathVariable("id") @Min(value = 1, message = "{validation.id.min}") Long id) {
    service.delete(id);
    String message =
        messageSource.getMessage("industry.delete.success", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.noData(HttpStatus.OK, message);
  }
}
