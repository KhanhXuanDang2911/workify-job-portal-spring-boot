package beworkify.controller;

import beworkify.dto.request.DistrictRequest;
import beworkify.dto.response.DistrictResponse;
import beworkify.dto.response.ResponseData;
import beworkify.service.DistrictService;
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
 * REST controller for managing districts. Provides endpoints for retrieving and managing district
 * data.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/districts")
public class DistrictController {

  private final DistrictService service;
  private final MessageSource messageSource;

  @GetMapping
  public ResponseEntity<ResponseData<java.util.List<DistrictResponse>>> getAll() {
    List<DistrictResponse> response = service.getAll();
    String message =
        messageSource.getMessage(
            "district.get.list.success", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.withData(HttpStatus.OK, message, response);
  }

  @GetMapping("/province/{provinceId}")
  public ResponseEntity<ResponseData<java.util.List<DistrictResponse>>> getByProvinceId(
      @PathVariable("provinceId") @Min(value = 1, message = "{validation.id.min}")
          Long provinceId) {
    List<DistrictResponse> response = service.getByProvinceId(provinceId);
    String message =
        messageSource.getMessage(
            "district.get.list.success", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.withData(HttpStatus.OK, message, response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ResponseData<DistrictResponse>> getById(
      @PathVariable("id") @Min(value = 1, message = "{validation.id.min}") Long id) {
    DistrictResponse dto = service.getById(id);
    String message =
        messageSource.getMessage("district.get.success", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.withData(HttpStatus.OK, message, dto);
  }

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ResponseData<DistrictResponse>> create(
      @Valid @RequestBody DistrictRequest request) {
    DistrictResponse dto = service.create(request);
    String message =
        messageSource.getMessage("district.create.success", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.withData(HttpStatus.CREATED, message, dto);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ResponseData<DistrictResponse>> update(
      @PathVariable("id") @Min(value = 1, message = "{validation.id.min}") Long id,
      @Valid @RequestBody DistrictRequest request) {
    DistrictResponse dto = service.update(id, request);
    String message =
        messageSource.getMessage("district.update.success", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.withData(HttpStatus.OK, message, dto);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ResponseData<Void>> delete(
      @PathVariable("id") @Min(value = 1, message = "{validation.id.min}") Long id) {
    service.delete(id);
    String message =
        messageSource.getMessage("district.delete.success", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.noData(HttpStatus.OK, message);
  }
}
