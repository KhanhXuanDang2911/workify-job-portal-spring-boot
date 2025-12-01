package beworkify.controller;

import beworkify.dto.request.ProvinceRequest;
import beworkify.dto.response.ProvinceResponse;
import beworkify.dto.response.ResponseData;
import beworkify.service.ProvinceService;
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
 * REST controller for managing provinces. Provides endpoints for retrieving and managing province
 * data.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/provinces")
public class ProvinceController {

  private final ProvinceService service;
  private final MessageSource messageSource;

  @GetMapping
  public ResponseEntity<ResponseData<List<ProvinceResponse>>> getAll() {
    List<ProvinceResponse> response = service.getAll();
    String message =
        messageSource.getMessage(
            "province.get.list.success", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.withData(HttpStatus.OK, message, response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ResponseData<ProvinceResponse>> getById(
      @PathVariable("id") @Min(value = 1, message = "{validation.id.min}") Long id) {
    ProvinceResponse dto = service.getById(id);
    String message =
        messageSource.getMessage("province.get.success", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.withData(HttpStatus.OK, message, dto);
  }

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ResponseData<ProvinceResponse>> create(
      @Valid @RequestBody ProvinceRequest request) {
    ProvinceResponse dto = service.create(request);
    String message =
        messageSource.getMessage("province.create.success", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.withData(HttpStatus.CREATED, message, dto);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ResponseData<ProvinceResponse>> update(
      @PathVariable("id") @Min(value = 1, message = "{validation.id.min}") Long id,
      @Valid @RequestBody ProvinceRequest request) {
    ProvinceResponse dto = service.update(id, request);
    String message =
        messageSource.getMessage("province.update.success", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.withData(HttpStatus.OK, message, dto);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ResponseData<Void>> delete(
      @PathVariable("id") @Min(value = 1, message = "{validation.id.min}") Long id) {
    service.delete(id);
    String message =
        messageSource.getMessage("province.delete.success", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.noData(HttpStatus.OK, message);
  }
}
