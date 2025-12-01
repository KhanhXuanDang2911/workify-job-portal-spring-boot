package beworkify.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;

/**
 * DTO for job category response. Contains category details and popular industries.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryJobResponse extends BaseResponse {
  private Long id;
  private String name;
  private String description;
  private String engName;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private List<PopularIndustryResponse> industries;
}
