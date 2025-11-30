package beworkify.controller;

import beworkify.dto.request.CategoryPostRequest;
import beworkify.dto.response.CategoryPostResponse;
import beworkify.dto.response.PageResponse;
import beworkify.dto.response.ResponseData;
import beworkify.service.CategoryPostService;
import beworkify.util.ResponseBuilder;
import beworkify.validation.group.OnCreate;
import beworkify.validation.group.OnUpdate;
import jakarta.validation.constraints.Min;
import jakarta.validation.groups.Default;
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
@RequestMapping("/api/v1/categories-post")
public class CategoryPostController {

  private final CategoryPostService service;
  private final MessageSource messageSource;

  @GetMapping("/all")
  public ResponseEntity<ResponseData<List<CategoryPostResponse>>> getAll() {
    log.info("Request: Get all categories post");
    List<CategoryPostResponse> response = service.getAll();
    String message =
        messageSource.getMessage(
            "categoryPost.get.list.success", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.withData(HttpStatus.OK, message, response);
  }

  @GetMapping
  public ResponseEntity<ResponseData<PageResponse<List<CategoryPostResponse>>>>
      getAllWithPagination(
          @RequestParam(defaultValue = "1")
              @Min(value = 1, message = "{validation.page.number.min}")
              int pageNumber,
          @RequestParam(defaultValue = "10") @Min(value = 1, message = "{validation.page.size.min}")
              int pageSize,
          @RequestParam(required = false) List<String> sorts,
          @RequestParam(defaultValue = "") String keyword) {
    log.info(
        "Request: Get categories post with pageNumber={}, pageSize={}, sorts={}, keyword={}",
        pageNumber,
        pageSize,
        sorts,
        keyword);
    PageResponse<List<CategoryPostResponse>> response =
        service.getAllWithPaginationAndSort(pageNumber, pageSize, sorts, keyword);
    String message =
        messageSource.getMessage(
            "categoryPost.get.list.success", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.withData(HttpStatus.OK, message, response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ResponseData<CategoryPostResponse>> getById(
      @PathVariable("id") @Min(value = 1, message = "{validation.id.min}") Long id) {
    log.info("Request: Get category post by id = {}", id);
    CategoryPostResponse dto = service.getById(id);
    String message =
        messageSource.getMessage("categoryPost.get.success", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.withData(HttpStatus.OK, message, dto);
  }

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ResponseData<CategoryPostResponse>> create(
      @Validated({OnCreate.class, Default.class}) @RequestBody CategoryPostRequest request) {
    log.info("Request: Create category post = {}", request);
    CategoryPostResponse dto = service.create(request);
    String message =
        messageSource.getMessage(
            "categoryPost.create.success", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.withData(HttpStatus.CREATED, message, dto);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ResponseData<CategoryPostResponse>> update(
      @PathVariable("id") @Min(value = 1, message = "{validation.id.min}") Long id,
      @Validated({OnUpdate.class, Default.class}) @RequestBody CategoryPostRequest request) {
    log.info("Request: Update category post id = {}, data = {}", id, request);
    CategoryPostResponse dto = service.update(id, request);
    String message =
        messageSource.getMessage(
            "categoryPost.update.success", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.withData(HttpStatus.OK, message, dto);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ResponseData<Void>> delete(
      @PathVariable("id") @Min(value = 1, message = "{validation.id.min}") Long id) {
    log.info("Request: Delete category post id = {}", id);
    service.delete(id);
    String message =
        messageSource.getMessage(
            "categoryPost.delete.success", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.noData(HttpStatus.OK, message);
  }
}
