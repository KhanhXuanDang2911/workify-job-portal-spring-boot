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
@RequestMapping("/api/v1/industries")
public class IndustryController {

  private final IndustryService service;
  private final MessageSource messageSource;

  @GetMapping("/all")
  public ResponseEntity<ResponseData<List<IndustryResponse>>> getAll() {
    log.info("Request: Get all industries");
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
    log.info(
        "Request: Get industries with pageNumber={}, pageSize={}, sorts={}, keyword={}",
        pageNumber,
        pageSize,
        sorts,
        keyword);
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
    log.info("Request: Get industry by id = {}", id);
    IndustryResponse dto = service.getById(id);
    String message =
        messageSource.getMessage("industry.get.success", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.withData(HttpStatus.OK, message, dto);
  }

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ResponseData<IndustryResponse>> create(
      @Valid @RequestBody IndustryRequest request) {
    log.info("Request: Create industry = {}", request);
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
    log.info("Request: Update industry id = {}, data = {}", id, request);
    IndustryResponse dto = service.update(id, request);
    String message =
        messageSource.getMessage("industry.update.success", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.withData(HttpStatus.OK, message, dto);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ResponseData<Void>> delete(
      @PathVariable("id") @Min(value = 1, message = "{validation.id.min}") Long id) {
    log.info("Request: Delete industry id = {}", id);
    service.delete(id);
    String message =
        messageSource.getMessage("industry.delete.success", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.noData(HttpStatus.OK, message);
  }
}
