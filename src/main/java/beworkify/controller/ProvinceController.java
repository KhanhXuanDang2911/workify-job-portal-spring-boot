package beworkify.controller;

import beworkify.dto.request.ProvinceRequest;
import beworkify.dto.response.ProvinceResponse;
import beworkify.dto.response.PageResponse;
import beworkify.dto.response.ResponseData;
import beworkify.service.ProvinceService;
import beworkify.util.ResponseBuilder;
import beworkify.validation.OnCreate;
import beworkify.validation.OnUpdate;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/provinces")
public class ProvinceController {

    private final ProvinceService service;
    private final MessageSource messageSource;

    @GetMapping
    public ResponseEntity<ResponseData<List<ProvinceResponse>>> getAll() {
        log.info("Request: Get all provinces");
        java.util.List<ProvinceResponse> response = service.getAll();
        String message = messageSource.getMessage("province.get.list.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.OK, message, response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseData<ProvinceResponse>> getById(
            @PathVariable("id") @Min(value = 1, message = "{validation.id.min}") Long id) {
        log.info("Request: Get province by id = {}", id);
        ProvinceResponse dto = service.getById(id);
        String message = messageSource.getMessage("province.get.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.OK, message, dto);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<ProvinceResponse>> create(
            @Valid @RequestBody ProvinceRequest request) {
        log.info("Request: Create province = {}", request);
        ProvinceResponse dto = service.create(request);
        String message = messageSource.getMessage("province.create.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.CREATED, message, dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<ProvinceResponse>> update(
            @PathVariable("id") @Min(value = 1, message = "{validation.id.min}") Long id,
            @Valid @RequestBody ProvinceRequest request) {
        log.info("Request: Update province id = {}, data = {}", id, request);
        ProvinceResponse dto = service.update(id, request);
        String message = messageSource.getMessage("province.update.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.OK, message, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<Void>> delete(
            @PathVariable("id") @Min(value = 1, message = "{validation.id.min}") Long id) {
        log.info("Request: Delete province id = {}", id);
        service.delete(id);
        String message = messageSource.getMessage("province.delete.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.noData(HttpStatus.OK, message);
    }
}
