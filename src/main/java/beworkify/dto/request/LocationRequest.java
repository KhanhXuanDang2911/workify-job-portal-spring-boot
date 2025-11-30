package beworkify.dto.request;

import beworkify.validation.group.OnCreate;
import beworkify.validation.group.OnUpdate;
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
public class LocationRequest {
  @NotNull(
      message = "{validation.location.provinceId.notNull}",
      groups = {OnCreate.class, OnUpdate.class})
  private Long provinceId;

  @NotNull(
      message = "{validation.location.districtId.notNull}",
      groups = {OnCreate.class, OnUpdate.class})
  private Long districtId;

  @Size(
      max = 1000,
      message = "{validation.location.detailAddress.size}",
      groups = {OnCreate.class, OnUpdate.class})
  private String detailAddress;
}
