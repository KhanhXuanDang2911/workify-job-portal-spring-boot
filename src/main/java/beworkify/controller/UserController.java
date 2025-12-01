package beworkify.controller;

import beworkify.dto.request.UpdatePasswordRequest;
import beworkify.dto.request.UserRequest;
import beworkify.dto.response.PageResponse;
import beworkify.dto.response.ResponseData;
import beworkify.dto.response.UserResponse;
import beworkify.service.UserService;
import beworkify.util.AppUtils;
import beworkify.util.ResponseBuilder;
import beworkify.validation.annotation.ValidImageFile;
import beworkify.validation.group.OnAdmin;
import beworkify.validation.group.OnCreate;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.groups.Default;
import java.io.UnsupportedEncodingException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST controller for managing users (job seekers). Handles user registration, profile management,
 * and account operations.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/users")
public class UserController {

  private final UserService userService;
  private final MessageSource messageSource;

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping
  public ResponseEntity<ResponseData<PageResponse<List<UserResponse>>>>
      getUsersWithPaginationAndKeywordAndSorts(
          @RequestParam(defaultValue = "1")
              @Min(value = 1, message = "{validation.page.number.min}")
              int pageNumber,
          @RequestParam(defaultValue = "10") @Min(value = 1, message = "{validation.page.size.min}")
              int pageSize,
          @RequestParam(required = false) List<String> sorts,
          @RequestParam(defaultValue = "") String keyword) {
    PageResponse<List<UserResponse>> response =
        userService.getUsersWithPaginationAndKeywordAndSorts(pageNumber, pageSize, sorts, keyword);
    String message =
        messageSource.getMessage(
            "user.get.many.successfully", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.withData(HttpStatus.OK, message, response);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/{id}")
  public ResponseEntity<ResponseData<UserResponse>> getUserById(
      @Min(value = 1, message = "{validation.id.min}") @PathVariable Long id) {
    UserResponse response = userService.getUserById(id);
    String message =
        messageSource.getMessage(
            "user.get.one.successfully", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.withData(HttpStatus.OK, message, response);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping(
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ResponseData<UserResponse>> createUser(
      @RequestPart(value = "avatar", required = false)
          @ValidImageFile(required = false, message = "image.file.invalid")
          MultipartFile avatar,
      @RequestPart("user") @Validated({Default.class, OnAdmin.class, OnCreate.class})
          UserRequest request) {
    UserResponse response = userService.createUser(request, avatar);
    String message =
        messageSource.getMessage("user.create.successfully", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.withData(HttpStatus.CREATED, message, response);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping(
      value = "/{id}",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ResponseData<UserResponse>> updateUser(
      @Min(value = 1, message = "{validation.id.min}}") @PathVariable Long id,
      @RequestPart(value = "avatar", required = false)
          @ValidImageFile(required = false, message = "image.file.invalid")
          MultipartFile avatar,
      @RequestPart("user") @Validated({Default.class, OnAdmin.class}) UserRequest request) {
    UserResponse response = userService.updateUser(request, avatar, id);
    String message =
        messageSource.getMessage("user.update.successfully", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.withData(HttpStatus.OK, message, response);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @DeleteMapping("/{id}")
  public ResponseEntity<ResponseData<Void>> deleteUser(
      @PathVariable("id") @Min(value = 1, message = "{validation.id.min}") Long id) {
    userService.deleteUser(id);
    String message =
        messageSource.getMessage("user.delete.successfully", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.noData(HttpStatus.OK, message);
  }

  @PostMapping("/sign-up")
  public ResponseEntity<ResponseData<UserResponse>> signUp(
      @RequestBody @Validated({Default.class, OnCreate.class}) UserRequest request,
      @RequestHeader("User-Agent") String userAgent)
      throws MessagingException, UnsupportedEncodingException {
    boolean isMobile = AppUtils.isMobile(userAgent);
    UserResponse response = userService.signUp(request, isMobile);
    String message =
        messageSource.getMessage(
            "user.sign.up.successfully", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.withData(HttpStatus.CREATED, message, response);
  }

  @PreAuthorize("hasRole('JOB_SEEKER') or hasRole('ADMIN')")
  @GetMapping("/me")
  public ResponseEntity<ResponseData<UserResponse>> getProfile() {
    Long userId = AppUtils.getUserIdFromSecurityContext();
    UserResponse response = userService.getUserById(userId);
    String message =
        messageSource.getMessage("user.profile.get.success", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.withData(HttpStatus.OK, message, response);
  }

  @PutMapping("/me")
  @PreAuthorize("hasRole('JOB_SEEKER') or hasRole('ADMIN')")
  public ResponseEntity<ResponseData<UserResponse>> updateProfile(
      @Valid @RequestBody UserRequest request) {
    Long userId = AppUtils.getUserIdFromSecurityContext();

    UserResponse response = userService.updateProfile(userId, request);

    String message =
        messageSource.getMessage(
            "user.profile.update.success", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.withData(HttpStatus.OK, message, response);
  }

  @PreAuthorize("hasRole('JOB_SEEKER') or hasRole('ADMIN')")
  @PatchMapping("/me/avatar")
  public ResponseEntity<ResponseData<UserResponse>> updateAvatar(
      @RequestPart(value = "avatar") @ValidImageFile(message = "image.file.invalid")
          MultipartFile avatar) {
    Long userId = AppUtils.getUserIdFromSecurityContext();
    UserResponse response = userService.updateAvatar(userId, avatar);
    String message =
        messageSource.getMessage(
            "user.avatar.update.success", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.withData(HttpStatus.OK, message, response);
  }

  @PreAuthorize("hasRole('JOB_SEEKER') or hasRole('ADMIN')")
  @PatchMapping("/me/password")
  public ResponseEntity<ResponseData<Void>> changePassword(
      @Valid @RequestBody UpdatePasswordRequest request) {
    Long userId = AppUtils.getUserIdFromSecurityContext();
    userService.updatePassword(userId, request);
    String message =
        messageSource.getMessage(
            "user.password.update.success", null, LocaleContextHolder.getLocale());
    return ResponseBuilder.noData(HttpStatus.OK, message);
  }
}
