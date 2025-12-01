package beworkify.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

/**
 * DTO for authentication token response. Contains access token, refresh token, and optional user
 * data.
 *
 * @param <T> Type of the user data.
 * @author KhanhDX
 * @since 1.0.0
 */
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
