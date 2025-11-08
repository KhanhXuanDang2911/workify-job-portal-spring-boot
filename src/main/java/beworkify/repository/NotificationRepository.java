
package beworkify.repository;

import beworkify.entity.Employer;
import beworkify.entity.Notification;
import beworkify.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
	Page<Notification> findByRecipientUserOrderByCreatedAtDesc(User user, Pageable pageable);

	Page<Notification> findByRecipientEmployerOrderByCreatedAtDesc(Employer employer, Pageable pageable);

	long countByRecipientUserAndReadFlagIsFalse(User user);

	long countByRecipientEmployerAndReadFlagIsFalse(Employer employer);
}
