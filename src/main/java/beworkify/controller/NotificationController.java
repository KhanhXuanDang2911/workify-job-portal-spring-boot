
package beworkify.controller;

import beworkify.dto.response.NotificationResponse;
import beworkify.dto.response.PageResponse;
import beworkify.dto.response.ResponseData;
import beworkify.service.NotificationService;
import beworkify.util.ResponseBuilder;
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

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Validated
public class NotificationController {

	private final NotificationService notificationService;
	private final MessageSource messageSource;

	@GetMapping
	@PreAuthorize("hasAnyRole('JOB_SEEKER','EMPLOYER','ADMIN')")
	public ResponseEntity<ResponseData<PageResponse<List<NotificationResponse>>>> getMyNotifications(
			@RequestParam(defaultValue = "1") @Min(value = 1, message = "{validation.page.number.min}") int pageNumber,
			@RequestParam(defaultValue = "10") @Min(value = 1, message = "{validation.page.size.min}") int pageSize) {
		PageResponse<List<NotificationResponse>> response = notificationService.getMyNotifications(pageNumber,
				pageSize);
		String message = messageSource.getMessage("notification.get.success", null, LocaleContextHolder.getLocale());
		return ResponseBuilder.withData(HttpStatus.OK, message, response);
	}

	@PostMapping("/read-all")
	@PreAuthorize("hasAnyRole('JOB_SEEKER','EMPLOYER','ADMIN')")
	public ResponseEntity<ResponseData<Void>> markAllAsRead() {
		notificationService.markAllAsRead();
		String message = messageSource.getMessage("notification.read.all.success", null,
				LocaleContextHolder.getLocale());
		return ResponseBuilder.noData(HttpStatus.OK, message);
	}

	@PostMapping("/{id}/read")
	@PreAuthorize("hasAnyRole('JOB_SEEKER','EMPLOYER','ADMIN')")
	public ResponseEntity<ResponseData<Void>> markAsRead(@PathVariable("id") Long id) {
		notificationService.markAsRead(id);
		String message = messageSource.getMessage("notification.read.success", null, LocaleContextHolder.getLocale());
		return ResponseBuilder.noData(HttpStatus.OK, message);
	}

	@GetMapping("/unread-count")
	@PreAuthorize("hasAnyRole('JOB_SEEKER','EMPLOYER','ADMIN')")
	public ResponseEntity<ResponseData<Long>> countUnread() {
		long count = notificationService.countUnread();
		String message = messageSource.getMessage("notification.count.success", null, LocaleContextHolder.getLocale());
		return ResponseBuilder.withData(HttpStatus.OK, message, count);
	}
}
