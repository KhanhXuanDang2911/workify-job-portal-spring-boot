package beworkify.dto.response;

import lombok.*;

/**
 * DTO for user summary response. Contains essential user details for list views or summaries.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSummaryResponse {
  private Long id;
  private String email;
  private String fullName;
  private String avatarUrl;
  private String role;
}
