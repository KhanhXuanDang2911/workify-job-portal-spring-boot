
package beworkify.dto.response;

import lombok.*;

import java.time.LocalDateTime;

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
