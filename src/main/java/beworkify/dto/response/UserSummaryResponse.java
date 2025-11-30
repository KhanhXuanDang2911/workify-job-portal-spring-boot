package beworkify.dto.response;

import lombok.*;

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
