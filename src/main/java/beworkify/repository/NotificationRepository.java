package beworkify.repository;

import beworkify.entity.Employer;
import beworkify.entity.Notification;
import beworkify.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Notification entities. Provides methods for CRUD operations and
 * custom queries related to notifications.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
  Page<Notification> findByRecipientUserOrderByCreatedAtDesc(User user, Pageable pageable);

  Page<Notification> findByRecipientEmployerOrderByCreatedAtDesc(
      Employer employer, Pageable pageable);

  long countByRecipientUserAndReadFlagIsFalse(User user);

  long countByRecipientEmployerAndReadFlagIsFalse(Employer employer);
}
