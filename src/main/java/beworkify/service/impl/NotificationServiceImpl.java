
package beworkify.service.impl;

import beworkify.dto.response.NotificationResponse;
import beworkify.dto.response.PageResponse;
import beworkify.entity.Employer;
import beworkify.entity.Notification;
import beworkify.entity.User;
import beworkify.exception.ResourceNotFoundException;
import beworkify.mapper.NotificationMapper;
import beworkify.repository.NotificationRepository;
import beworkify.service.NotificationService;
import beworkify.util.AppUtils;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

	private final NotificationRepository repository;
	private final NotificationMapper mapper;
	private final SimpMessagingTemplate messagingTemplate;
	private final MessageSource messageSource;

	@Override
	@Transactional
	public void notifyUser(User user, String title, String content, String type, String link, Long jobId,
			Long applicationId) {
		Notification notification = Notification.builder().recipientUser(user).title(title).content(content).type(type)
				.link(link).jobId(jobId).applicationId(applicationId).readFlag(false).build();
		Notification saved = repository.save(notification);
		NotificationResponse dto = mapper.toDTO(saved);
		messagingTemplate.convertAndSendToUser("USER:" + user.getEmail(), "/queue/notifications", dto);
	}

	@Override
	@Transactional
	public void notifyEmployer(Employer employer, String title, String content, String type, String link, Long jobId,
			Long applicationId) {
		Notification notification = Notification.builder().recipientEmployer(employer).title(title).content(content)
				.type(type).link(link).jobId(jobId).applicationId(applicationId).readFlag(false).build();
		Notification saved = repository.save(notification);
		NotificationResponse dto = mapper.toDTO(saved);
		messagingTemplate.convertAndSendToUser("EMPLOYER:" + employer.getEmail(), "/queue/notifications", dto);
	}

	@Override
	@Transactional(readOnly = true)
	public PageResponse<List<NotificationResponse>> getMyNotifications(int pageNumber, int pageSize) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
		Page<Notification> page;
		if (AppUtils.hasRole(authentication, "EMPLOYER")) {
			Long employerId = AppUtils.getEmployerIdFromSecurityContext();
			Employer employer = new Employer();
			employer.setId(employerId);
			page = repository.findByRecipientEmployerOrderByCreatedAtDesc(employer, pageable);
		} else {
			Long userId = AppUtils.getUserIdFromSecurityContext();
			User user = new User();
			user.setId(userId);
			page = repository.findByRecipientUserOrderByCreatedAtDesc(user, pageable);
		}
		List<NotificationResponse> items = page.getContent().stream().map(mapper::toDTO).toList();
		return PageResponse.<List<NotificationResponse>>builder().pageNumber(pageNumber).pageSize(pageSize)
				.totalPages(page.getTotalPages()).numberOfElements(page.getNumberOfElements()).items(items).build();
	}

	@Override
	@Transactional
	public void markAsRead(Long id) {
		Notification notification = repository.findById(id).orElseThrow(() -> {
			String message = messageSource.getMessage("notification.not.found", null, LocaleContextHolder.getLocale());
			return new ResourceNotFoundException(message);
		});
		notification.setReadFlag(true);
		repository.save(notification);
	}

	@Override
	@Transactional
	public void markAllAsRead() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (AppUtils.hasRole(authentication, "EMPLOYER")) {
			Long employerId = AppUtils.getEmployerIdFromSecurityContext();
			Employer employer = new Employer();
			employer.setId(employerId);
			repository.findByRecipientEmployerOrderByCreatedAtDesc(employer, Pageable.unpaged()).forEach(n -> {
				if (!n.getReadFlag()) {
					n.setReadFlag(true);
					repository.save(n);
				}
			});
		} else {
			Long userId = AppUtils.getUserIdFromSecurityContext();
			User user = new User();
			user.setId(userId);
			repository.findByRecipientUserOrderByCreatedAtDesc(user, Pageable.unpaged()).forEach(n -> {
				if (!n.getReadFlag()) {
					n.setReadFlag(true);
					repository.save(n);
				}
			});
		}
	}

	@Override
	@Transactional(readOnly = true)
	public long countUnread() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (AppUtils.hasRole(authentication, "EMPLOYER")) {
			Long employerId = AppUtils.getEmployerIdFromSecurityContext();
			Employer employer = new Employer();
			employer.setId(employerId);
			return repository.countByRecipientEmployerAndReadFlagIsFalse(employer);
		} else {
			Long userId = AppUtils.getUserIdFromSecurityContext();
			User user = new User();
			user.setId(userId);
			return repository.countByRecipientUserAndReadFlagIsFalse(user);
		}
	}
}
