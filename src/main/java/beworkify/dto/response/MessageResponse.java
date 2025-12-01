package beworkify.dto.response;

import java.time.LocalDateTime;
import lombok.*;

/**
 * DTO for message response. Contains message content, sender details, and timestamp.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageResponse {
  private Long id;
  private Long conversationId;
  private Long senderId;
  private String senderType;
  private String senderName;
  private String senderAvatar;
  private String content;
  private Boolean seen;
  private LocalDateTime createdAt;
}
