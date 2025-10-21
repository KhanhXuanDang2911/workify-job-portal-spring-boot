package beworkify.controller;

import beworkify.dto.request.*;
import beworkify.dto.response.EmployerResponse;
import beworkify.dto.response.ResponseData;
import beworkify.dto.response.TokenResponse;
import beworkify.dto.response.UserResponse;
import beworkify.service.AuthenticationService;
import beworkify.service.EmployerService;
import beworkify.service.UserService;
import beworkify.util.AppUtils;
import beworkify.util.ResponseBuilder;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(value = "/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final EmployerService employerService;
    private final MessageSource messageSource;

    @PostMapping("/employers/sign-in")
    public ResponseEntity<ResponseData<TokenResponse<EmployerResponse>>> employerSignIn(
            @Validated @RequestBody SignInRequest request) {
        TokenResponse<EmployerResponse> response = authenticationService.employerSignIn(request);
        String message = messageSource.getMessage("user.sign.in.successfully", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.OK, message, response);
    }

    @PostMapping("/users/sign-in")
    public ResponseEntity<ResponseData<TokenResponse<UserResponse>>> userSignIn(
            @Validated @RequestBody SignInRequest request) {
        TokenResponse<UserResponse> response = authenticationService.userSignIn(request);
        String message = messageSource.getMessage("user.sign.in.successfully", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.OK, message, response);
    }

    @PostMapping("/users/refresh-token")
    public ResponseEntity<ResponseData<TokenResponse<Void>>> refreshTokenUser(
            @RequestHeader("Y-Token") String refreshToken) {
        TokenResponse<Void> response = authenticationService.refreshTokenUser(refreshToken);
        String message = messageSource.getMessage("auth.refresh.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.OK, message, response);
    }

    @PostMapping("/employers/refresh-token")
    public ResponseEntity<ResponseData<TokenResponse<Void>>> refreshTokenEmployer(
            @RequestHeader("Y-Token") String refreshToken) {
        TokenResponse<Void> response = authenticationService.refreshTokenEmployer(refreshToken);
        String message = messageSource.getMessage("auth.refresh.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.OK, message, response);
    }

    @PostMapping("/sign-out")
    public ResponseEntity<ResponseData<Void>> signOut(@RequestHeader("X-Token") String accessToken,
            @RequestHeader("Y-Token") String refreshToken) {
        authenticationService.signOut(accessToken, refreshToken);
        String message = messageSource.getMessage("auth.logout.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.noData(HttpStatus.OK, message);
    }

    @PatchMapping(value = "/users/verify-email")
    public ResponseEntity<ResponseData<Void>> verifyEmailUser(@RequestHeader("C-Token") String confirmToken) {
        userService.verifyEmailUser(confirmToken);
        String message = messageSource.getMessage("user.verify.email.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.noData(HttpStatus.OK, message);
    }

    @PatchMapping(value = "/employers/verify-email")
    public ResponseEntity<ResponseData<Void>> verifyEmailEmployer(@RequestHeader("C-Token") String confirmToken) {
        employerService.verifyEmailEmployer(confirmToken);
        String message = messageSource.getMessage("user.verify.email.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.noData(HttpStatus.OK, message);
    }

    @PatchMapping(value = "/users/mobile/verify-email")
    public ResponseEntity<ResponseData<Void>> verifyEmailUserMobile(@Valid @RequestBody VerifyEmailMobileRequest request) {
        userService.verifyEmailUserMobile(request);
        String message = messageSource.getMessage("user.verify.email.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.noData(HttpStatus.OK, message);
    }

    @PatchMapping(value = "/employers/mobile/verify-email")
    public ResponseEntity<ResponseData<Void>> verifyEmailEmployerMobile(@Valid @RequestBody VerifyEmailMobileRequest request) {
        employerService.verifyEmailEmployerMobile(request);
        String message = messageSource.getMessage("user.verify.email.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.noData(HttpStatus.OK, message);
    }

    @PostMapping(value = "/users/forgot-password")
    public ResponseEntity<ResponseData<Void>> forgotPasswordUser(@Valid @RequestBody ForgotPasswordRequest request,
                                                                 @RequestHeader("User-Agent") String userAgent)
            throws MessagingException, UnsupportedEncodingException {
        boolean isMobile = AppUtils.isMobile(userAgent);
        userService.forgotPassword(request, isMobile);
        String message = messageSource.getMessage("auth.forgot.password.success", null,
                LocaleContextHolder.getLocale());
        return ResponseBuilder.noData(HttpStatus.OK, message);
    }

    @PostMapping(value = "/employers/forgot-password")
    public ResponseEntity<ResponseData<Void>> forgotPasswordEmployer(
            @Valid @RequestBody ForgotPasswordRequest request,
            @RequestHeader("User-Agent") String userAgent)
            throws MessagingException, UnsupportedEncodingException {
        boolean isMobile = AppUtils.isMobile(userAgent);
        employerService.forgotPassword(request, isMobile);
        String message = messageSource.getMessage("auth.forgot.password.success", null,
                LocaleContextHolder.getLocale());
        return ResponseBuilder.noData(HttpStatus.OK, message);
    }

    @PostMapping("/users/reset-password")
    public ResponseEntity<ResponseData<Void>> resetPasswordUser(@RequestHeader("R-Token") String token,
            @Valid @RequestBody ResetPasswordRequest request) {
        userService.resetPassword(token, request);
        String message = messageSource.getMessage("auth.reset.password.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.noData(HttpStatus.OK, message);
    }

    @PostMapping("/users/mobile/reset-password")
    public ResponseEntity<ResponseData<Void>> resetPasswordUserMobile(@Valid @RequestBody ResetPasswordMobileRequest request) {
        userService.resetPasswordUserMobile(request);
        String message = messageSource.getMessage("auth.reset.password.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.noData(HttpStatus.OK, message);
    }

    @PostMapping("/employers/reset-password")
    public ResponseEntity<ResponseData<Void>> resetPasswordEmployer(@RequestHeader("R-Token") String token,
            @Valid @RequestBody ResetPasswordRequest request) {
        employerService.resetPassword(token, request);
        String message = messageSource.getMessage("auth.reset.password.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.noData(HttpStatus.OK, message);
    }

    @PostMapping("/employers/mobile/reset-password")
    public ResponseEntity<ResponseData<Void>> resetPasswordEmployerMobile(@Valid @RequestBody ResetPasswordMobileRequest request) {
        employerService.resetPasswordEmployerMobile(request);
        String message = messageSource.getMessage("auth.reset.password.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.noData(HttpStatus.OK, message);
    }

    @PostMapping("/authenticate/google")
    public ResponseEntity<ResponseData<TokenResponse<UserResponse>>> authenticateGoogle(
            @RequestHeader("G-Code") String code) {
        log.info("Authenticating user with Google code: {}", code);
        TokenResponse<UserResponse> response = authenticationService.authenticateGoogle(code);
        String message = messageSource.getMessage("auth.google.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.OK, message, response);
    }

    @PostMapping("/authenticate/linkedin")
    public ResponseEntity<ResponseData<TokenResponse<UserResponse>>> authenticateLinkedIn(
            @RequestHeader("L-Code") String code) {
        log.info("Authenticating user with LinkedIn code: {}", code);
        TokenResponse<UserResponse> response = authenticationService.authenticateLinkedIn(code);
        String message = messageSource.getMessage("auth.linkedin.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.OK, message, response);
    }

    @PostMapping(value = "/create-password")
    public ResponseEntity<ResponseData<TokenResponse<UserResponse>>> createPassword(
            @RequestHeader("CR-Token") String token,
            @RequestBody UserCreationPasswordRequest request) {
        TokenResponse<UserResponse> response = userService.createPassword(token, request);
        String message = messageSource.getMessage("user.password.create.success", null,
                LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.OK, message, response);
    }

}
