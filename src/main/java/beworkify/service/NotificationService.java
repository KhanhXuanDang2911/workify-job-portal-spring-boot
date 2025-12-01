package beworkify.service;

import beworkify.dto.response.NotificationResponse;
import beworkify.dto.response.PageResponse;
import beworkify.entity.Employer;
import beworkify.entity.User;
import java.util.List;

/**
 * Service interface for managing user notifications. Handles creating and managing notifications
 * for users and employers.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
public interface NotificationService {
  void notifyUser(
      User user,
      String title,
      String content,
      String type,
      String link,
      Long jobId,
      Long applicationId);

  void notifyEmployer(
      Employer employer,
      String title,
      String content,
      String type,
      String link,
      Long jobId,
      Long applicationId);

  PageResponse<List<NotificationResponse>> getMyNotifications(int pageNumber, int pageSize);

  void markAsRead(Long id);

  void markAllAsRead();

  long countUnread();
}
