package beworkify.dto.response;

import java.time.LocalDateTime;
import lombok.*;

/**
 * DTO for conversation response. Contains details about the job, participants, and the last
 * message.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationResponse {
  private Long id;
  private Long jobId;
  private String jobTitle;
  private Long applicationId;
  private Long jobSeekerId;
  private String jobSeekerName;
  private String jobSeekerAvatar;
  private Long employerId;
  private String employerName;
  private String employerAvatar;
  private String lastMessage;
  private Long lastMessageSenderId;
  private String lastMessageSenderType;
  private Boolean hasEmployerMessage;
  private Boolean hasUnread;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
