package beworkify.dto.response;

import java.time.LocalDateTime;
import lombok.*;

/**
 * DTO for notification response. Contains notification details and read status.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponse {
  private Long id;
  private String title;
  private String content;
  private String type;
  private String link;
  private Long jobId;
  private Long applicationId;
  private Boolean readFlag;
  private LocalDateTime createdAt;
}
