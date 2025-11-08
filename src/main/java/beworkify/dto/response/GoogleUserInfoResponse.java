
package beworkify.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GoogleUserInfoResponse {
	private String sub;
	private String name;

	@JsonProperty("given_name")
	private String givenName;

	@JsonProperty("family_name")
	private String familyName;

	private String picture;
	private String email;

	@JsonProperty("email_verified")
	private Boolean emailVerified;

	private String locale;
}
