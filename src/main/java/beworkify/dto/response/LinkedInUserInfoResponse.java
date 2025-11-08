
package beworkify.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LinkedInUserInfoResponse {
	private String name;
	private String email;
	private String picture;
}
