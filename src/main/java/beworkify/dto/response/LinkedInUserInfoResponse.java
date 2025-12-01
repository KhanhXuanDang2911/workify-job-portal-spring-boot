package beworkify.dto.response;

import lombok.*;

/**
 * DTO for LinkedIn user info response.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
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
