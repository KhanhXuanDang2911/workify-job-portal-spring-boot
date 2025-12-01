package beworkify.dto.response;

import lombok.*;

/**
 * DTO for popular industry response. Contains industry details and job count.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PopularIndustryResponse {
  private Long id;
  private String name;
  private String engName;
  private String description;
  private Long jobCount;
}
