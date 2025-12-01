package beworkify.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * DTO for province response.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ProvinceResponse extends BaseResponse {
  private String code;
  private String name;
  private String engName;
  private String provinceSlug;
}
