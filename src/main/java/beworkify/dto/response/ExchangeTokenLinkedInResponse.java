package beworkify.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

/**
 * DTO for LinkedIn token exchange response.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ExchangeTokenLinkedInResponse {
  private String accessToken;
  private Long expiresIn;
  private String refreshToken;
  private String refreshTokenExpiresIn;
  private String scope;
}
