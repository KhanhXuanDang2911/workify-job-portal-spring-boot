
package beworkify.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DistrictRequest {
	@NotBlank(message = "{validation.province.code.notBlank}")
	@Size(max = 50, message = "{validation.province.code.size}")
	private String code;

	@NotBlank(message = "{validation.province.name.notBlank}")
	@Size(max = 255, message = "{validation.province.name.size}")
	private String name;

	@NotNull(message = "{validation.id.min}")
	private Long provinceId;
}
