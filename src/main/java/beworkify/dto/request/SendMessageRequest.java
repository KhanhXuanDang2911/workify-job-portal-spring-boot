
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

	@NotNull(message = "Receiver ID is required")
	private Long receiverId;

	@NotBlank(message = "Content is required")
	private String content;

	private Long conversationId;

	private Long jobId;

	private Long applicationId;
}
