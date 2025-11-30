package beworkify.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

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
