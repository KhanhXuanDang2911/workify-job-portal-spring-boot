package beworkify.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

/**
 * DTO for exchanging Google authorization code for tokens.
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
public class ExchangeTokenGoogleRequest {
  String code;
  String clientId;
  String clientSecret;
  String redirectUri;
  String grantType;
}
