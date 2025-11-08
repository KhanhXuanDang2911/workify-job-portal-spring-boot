
package beworkify.dto.request;

import beworkify.validation.group.OnLinkApply;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationRequest {
	@NotBlank(message = "{validation.fullname.not.blank}")
	@Size(min = 3, max = 160, message = "{validation.fullname.size}")
	private String fullName;

	@NotBlank(message = "{validation.email.not.blank}")
	@Pattern(regexp = "^[a-zA-Z0-9](?:[a-zA-Z0-9._%+-]{0,63}[a-zA-Z0-9])?@[a-zA-Z0-9](?:[a-zA-Z0-9.-]{0,253}[a-zA-Z0-9])?\\.[a-zA-Z]{2,}$", message = "{validation.email.invalid}")
	private String email;

	@Pattern(regexp = "^(?:\\+84|0)[35789][0-9]{8}$", message = "{validation.phone.invalid}")
	@NotBlank(message = "{validation.phone.number.not.null}")
	private String phoneNumber;

	@NotBlank(message = "{validation.cover.letter.not.blank}")
	@Size(max = 1000, message = "{validation.cover.letter.size}")
	private String coverLetter;

	@NotNull(message = "{validation.job.id.not.null}")
	@Min(value = 1, message = "{validation.job.id.min}")
	private Long jobId;

	@NotNull(message = "{validation.cv.url.not.null}", groups = {OnLinkApply.class})
	private String cvUrl;
}
