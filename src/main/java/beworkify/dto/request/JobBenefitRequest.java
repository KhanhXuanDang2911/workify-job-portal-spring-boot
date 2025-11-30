package beworkify.dto.request;

import beworkify.enumeration.BenefitType;
import beworkify.validation.annotation.ValueOfEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobBenefitRequest {
  @ValueOfEnum(enumClass = BenefitType.class, message = "{error.invalid.job.benefit.enum}")
  private String type;

  @NotBlank(message = "{validation.job.benefits.description.notBlank}")
  @Size(max = 1000, message = "{validation.job.benefits.description.size}")
  private String description;
}
