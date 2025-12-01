package beworkify.dto.request;

import beworkify.validation.group.OnCreate;
import beworkify.validation.group.OnUpdate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for creating or updating a province.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProvinceRequest {
  @NotBlank(message = "{validation.province.code.notBlank}")
  @Size(max = 50, message = "{validation.province.code.size}")
  private String code;

  @NotBlank(
      message = "{validation.province.name.notBlank}",
      groups = {OnCreate.class, OnUpdate.class})
  @Size(max = 255, message = "{validation.province.name.size}")
  private String name;

  @Size(max = 255, message = "{validation.province.engName.size}")
  private String engName;
}
