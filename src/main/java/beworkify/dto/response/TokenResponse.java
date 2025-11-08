
package beworkify.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TokenResponse<T> {
	private String accessToken;
	private String refreshToken;
	private T data;
	private String createPasswordToken;
}
