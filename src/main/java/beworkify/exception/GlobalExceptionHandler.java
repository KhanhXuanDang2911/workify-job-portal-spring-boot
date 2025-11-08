
package beworkify.exception;

import beworkify.dto.response.ErrorResponse;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import feign.FeignException;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mail.MailException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

	private final MessageSource messageSource;

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationBody(MethodArgumentNotValidException e, WebRequest request) {
		String message = messageSource.getMessage("error.validation.body.invalid", null,
				LocaleContextHolder.getLocale());
		return buildErrorResponse(HttpStatus.BAD_REQUEST, message, request,
				e.getBindingResult().getFieldErrors().stream().map(ex -> ErrorResponse.FieldError.builder()
						.fieldName(ex.getField()).message(ex.getDefaultMessage()).build())
						.collect(Collectors.toList()));
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> handleValidationParams(ConstraintViolationException e, WebRequest request) {
		String message = messageSource.getMessage("error.validation.params.invalid", null,
				LocaleContextHolder.getLocale());
		return buildErrorResponse(HttpStatus.BAD_REQUEST, message, request,
				e.getConstraintViolations().stream()
						.map(v -> ErrorResponse.FieldError.builder()
								.fieldName(v.getPropertyPath().toString()
										.substring(v.getPropertyPath().toString().lastIndexOf(".") + 1))
								.message(v.getMessage()).build())
						.collect(Collectors.toList()));
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorResponse> handleInvalidTypeParams(MethodArgumentTypeMismatchException e,
			WebRequest request) {
		String message = messageSource.getMessage("error.validation.type.mismatch",
				new Object[]{e.getName(), Objects.requireNonNull(e.getRequiredType()).getSimpleName()},
				LocaleContextHolder.getLocale());
		return buildErrorResponse(HttpStatus.BAD_REQUEST, message, request, null);
	}

	@ExceptionHandler(MissingRequestHeaderException.class)
	public ResponseEntity<ErrorResponse> handleMissingRequestHeader(MissingRequestHeaderException ex,
			WebRequest request) {
		String headerName = ex.getHeaderName();
		String message = messageSource.getMessage("error.missing.header", new Object[]{headerName},
				LocaleContextHolder.getLocale());
		return buildErrorResponse(HttpStatus.BAD_REQUEST, message, request, null);
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ErrorResponse> handleMissingParams(MissingServletRequestParameterException e,
			WebRequest request) {
		String message = messageSource.getMessage("error.validation.params.missing", new Object[]{e.getParameterName()},
				LocaleContextHolder.getLocale());
		return buildErrorResponse(HttpStatus.BAD_REQUEST, message, request, null);
	}

	@ExceptionHandler(MissingServletRequestPartException.class)
	public ResponseEntity<ErrorResponse> handleMissingRequestPart(MissingServletRequestPartException e,
			WebRequest request) {
		String message = messageSource.getMessage("error.validation.params.missing",
				new Object[]{e.getRequestPartName()}, LocaleContextHolder.getLocale());
		return buildErrorResponse(HttpStatus.BAD_REQUEST, message, request, null);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			WebRequest request) {
		String message = messageSource.getMessage("error.validation.body.not.readable", null,
				LocaleContextHolder.getLocale());

		Throwable cause = ex.getCause();
		if (cause instanceof InvalidFormatException ife) {
			String fieldName = "Unknown field";

			if (!ife.getPath().isEmpty()) {
				fieldName = ife.getPath().get(0).getFieldName();
			}

			Class<?> targetType = ife.getTargetType();

			if (targetType == Integer.class || targetType == Long.class || targetType == Double.class) {
				message = messageSource.getMessage("validation.field.number", new Object[]{fieldName},
						LocaleContextHolder.getLocale());
			} else if (targetType == LocalDate.class || targetType == Date.class) {
				message = messageSource.getMessage("validation.field.date", new Object[]{fieldName},
						LocaleContextHolder.getLocale());
			}
		}

		return buildErrorResponse(HttpStatus.BAD_REQUEST, message, request, null);
	}

	@ExceptionHandler(FeignException.BadRequest.class)
	public ResponseEntity<?> handleFeignBadRequest(FeignException.BadRequest ex, WebRequest request) {
		String message = messageSource.getMessage("error.oauth2", null, LocaleContextHolder.getLocale());
		return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, message, request, null);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException e, WebRequest request) {
		return buildErrorResponse(HttpStatus.NOT_FOUND, e.getMessage(), request, null);
	}

	@ExceptionHandler(ResourceConflictException.class)
	public ResponseEntity<ErrorResponse> handleResourceConflict(ResourceConflictException e, WebRequest request) {
		return buildErrorResponse(HttpStatus.CONFLICT, e.getMessage(), request, null);
	}

	@ExceptionHandler(MailException.class)
	public ResponseEntity<ErrorResponse> handleEmailException(MailException e, WebRequest request) {
		String message = messageSource.getMessage("error.sendMail", null, LocaleContextHolder.getLocale());
		return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, message, request, null);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException e, WebRequest request) {
		String message = messageSource.getMessage("error.accessDenied", null, LocaleContextHolder.getLocale());
		return buildErrorResponse(HttpStatus.FORBIDDEN, message, request, null);
	}

	@ExceptionHandler(UnAuthorizeException.class)
	public ResponseEntity<ErrorResponse> handleInvalidTokenException(UnAuthorizeException e, WebRequest request) {
		return buildErrorResponse(HttpStatus.UNAUTHORIZED, e.getMessage(), request, null);
	}

	@ExceptionHandler(InvalidTokenException.class)
	public ResponseEntity<ErrorResponse> handleInvalidTokenException(InvalidTokenException e, WebRequest request) {
		String message = messageSource.getMessage("auth.token.invalid", null, LocaleContextHolder.getLocale());
		return buildErrorResponse(HttpStatus.UNAUTHORIZED, message, request, null);
	}

	@ExceptionHandler(AccountStatusException.class)
	public ResponseEntity<ErrorResponse> handleAccountStatusException(AccountStatusException e, WebRequest request) {
		String message = messageSource.getMessage("error.disableAccount", null, LocaleContextHolder.getLocale());
		return buildErrorResponse(HttpStatus.UNAUTHORIZED, message, request, null);
	}

	@ExceptionHandler({UsernameNotFoundException.class, BadCredentialsException.class})
	public ResponseEntity<ErrorResponse> handleSignInException(Exception e, WebRequest request) {
		String message = messageSource.getMessage("error.invalid.email.password", null,
				LocaleContextHolder.getLocale());
		return buildErrorResponse(HttpStatus.UNAUTHORIZED, message, request, null);
	}

	@ExceptionHandler(InsufficientAuthenticationException.class)
	public ResponseEntity<ErrorResponse> handleInsufficientAuthenticationException(
			InsufficientAuthenticationException e, WebRequest request) {
		String message = messageSource.getMessage("error.insufficientAuthentication", null,
				LocaleContextHolder.getLocale());
		return buildErrorResponse(HttpStatus.UNAUTHORIZED, message, request, null);
	}

	@ExceptionHandler(NoHandlerFoundException.class)
	public ResponseEntity<ErrorResponse> handleNotFound(NoHandlerFoundException ex, WebRequest request) {
		String message = messageSource.getMessage("error.endpoint.notfound", null, LocaleContextHolder.getLocale());
		return buildErrorResponse(HttpStatus.NOT_FOUND, message, request, null);
	}

	// @ExceptionHandler(Exception.class)
	// public ResponseEntity<ErrorResponse> handleInternalError(Exception e,
	// WebRequest request) {
	// String message = messageSource.getMessage("error.internal.server", null,
	// LocaleContextHolder.getLocale());
	// return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, message, request,
	// null);
	// }

	@ExceptionHandler(AppException.class)
	public ResponseEntity<ErrorResponse> handleAppError(AppException e, WebRequest request) {
		String message = messageSource.getMessage(e.getMessage(), null, LocaleContextHolder.getLocale());
		return ResponseEntity.status(500)
				.body(ErrorResponse.builder().timestamp(LocalDateTime.now()).status(e.getErrorCode().getCode())
						.path(request.getDescription(false).replace("uri=", "")).error(message).message(message)
						.errors(null).build());
	}

	private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message, WebRequest request,
			List<ErrorResponse.FieldError> errors) {
		return ResponseEntity.status(status)
				.body(ErrorResponse.builder().timestamp(LocalDateTime.now()).status(status.value())
						.path(request.getDescription(false).replace("uri=", "")).error(status.getReasonPhrase())
						.message(message).errors(errors).build());
	}
}
