package beworkify.controller;

import beworkify.dto.request.PostRequest;
import beworkify.dto.response.PageResponse;
import beworkify.dto.response.PostResponse;
import beworkify.dto.response.ResponseData;
import beworkify.service.PostService;
import beworkify.util.AppUtils;
import beworkify.util.ResponseBuilder;
import beworkify.validation.OnCreate;
import beworkify.validation.OnUpdate;
import beworkify.validation.OnUserCreate;
import beworkify.validation.ValidImageFile;
import jakarta.validation.constraints.Min;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService service;
    private final MessageSource messageSource;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<PageResponse<List<PostResponse>>>> getAllPosts(
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "{validation.page.number.min}") int pageNumber,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "{validation.page.size.min}") int pageSize,
            @RequestParam(required = false) List<String> sorts,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false) Long categoryId) {
        log.info("Request: Get posts with pageNumber={}, pageSize={}, sorts={}, keyword={}", pageNumber, pageSize,
                sorts, keyword);
        PageResponse<List<PostResponse>> response;
        response = service.getAll(pageNumber, pageSize, sorts, keyword, categoryId, false, null);
        String message = messageSource.getMessage("post.get.list.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.OK, message, response);
    }

    @GetMapping("/public")
    public ResponseEntity<ResponseData<PageResponse<List<PostResponse>>>> getPublicPosts(
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "{validation.page.number.min}") int pageNumber,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "{validation.page.size.min}") int pageSize,
            @RequestParam(required = false) List<String> sorts,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false) Long categoryId) {
        PageResponse<List<PostResponse>> response = service.getAll(pageNumber, pageSize, sorts, keyword, categoryId,
                true, null);
        String message = messageSource.getMessage("post.get.list.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.OK, message, response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseData<PostResponse>> getById(
            @PathVariable("id") @Min(value = 1, message = "{validation.id.min}") Long id) {
        log.info("Request: Get post by id = {}", id);
        PostResponse dto = service.getById(id);
        String message = messageSource.getMessage("post.get.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.OK, message, dto);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<PostResponse>> create(
            @Validated({ OnCreate.class, Default.class }) @RequestPart("post") PostRequest request,
            @ValidImageFile @RequestPart("thumbnail") MultipartFile thumbnail) throws Exception {
        log.info("Request: Create post = {}", request);
        PostResponse dto = service.create(request, thumbnail);
        String message = messageSource.getMessage("post.create.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.CREATED, message, dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<PostResponse>> update(
            @PathVariable("id") @Min(value = 1, message = "{validation.id.min}") Long id,
            @Validated({ OnUpdate.class, Default.class }) @RequestPart("post") PostRequest request,
            @ValidImageFile(required = false, message = "{validation.image.file.invalid}") @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail)
            throws Exception {
        log.info("Request: Update post id = {}, data = {}", id, request);
        PostResponse dto = service.update(id, request, thumbnail);
        String message = messageSource.getMessage("post.update.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.OK, message, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseData<Void>> delete(
            @PathVariable("id") @Min(value = 1, message = "{validation.id.min}") Long id) {
        log.info("Request: Delete post id = {}", id);
        service.delete(id);
        String message = messageSource.getMessage("post.delete.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.noData(HttpStatus.OK, message);
    }

    @GetMapping("/public/{id}/related")
    public ResponseEntity<ResponseData<List<PostResponse>>> getRelated(
            @PathVariable("id") @Min(value = 1, message = "{validation.id.min}") Long id,
            @RequestParam(defaultValue = "6") @Min(value = 1, message = "{validation.page.size.min}") int limit) {
        log.info("Request: Get related posts for id={}, limit={}", id, limit);
        List<PostResponse> data = service.getRelated(id, Math.min(limit, 20));
        String message = messageSource.getMessage("post.get.list.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.OK, message, data);
    }

    @GetMapping("/public/latest")
    public ResponseEntity<ResponseData<List<PostResponse>>> getLatestPosts(
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "{validation.page.size.min}") int limit) {
        log.info("Request: Get latest posts with limit={}", limit);
        List<PostResponse> data = service.getLatestPosts(Math.min(limit, 50)); // Max 50
        String message = messageSource.getMessage("post.get.list.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.OK, message, data);
    }

}