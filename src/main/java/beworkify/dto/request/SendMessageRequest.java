package beworkify.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * DTO for sending a message in a conversation.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendMessageRequest {

  @NotNull(message = "Conversation ID is required")
  private Long conversationId;

  @NotBlank(message = "Content is required")
  private String content;
}
