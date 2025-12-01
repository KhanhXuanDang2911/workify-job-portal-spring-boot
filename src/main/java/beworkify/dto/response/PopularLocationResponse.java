package beworkify.dto.response;

import lombok.*;

/**
 * DTO for popular location response. Contains location details and job count.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PopularLocationResponse {
  private Long id;
  private String code;
  private String name;
  private String engName;
  private String provinceSlug;
  private Long jobCount;
}
