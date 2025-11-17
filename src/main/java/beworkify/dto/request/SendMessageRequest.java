
package beworkify.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Request DTO for sending a message in a conversation. Conversation is
 * automatically created when user applies for a job.
 * 
 * @param conversationId
 *            The ID of the conversation (required)
 * @param content
 *            The message content (required, not blank)
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
