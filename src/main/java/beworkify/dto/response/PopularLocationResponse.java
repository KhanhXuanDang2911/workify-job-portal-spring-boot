package beworkify.dto.response;

import lombok.*;

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
