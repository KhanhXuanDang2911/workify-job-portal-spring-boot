
package beworkify.dto.response;

import lombok.*;

import java.time.LocalDateTime;

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
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
