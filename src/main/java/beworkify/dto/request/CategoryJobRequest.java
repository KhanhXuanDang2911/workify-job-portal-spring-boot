package beworkify.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryJobRequest {

  @NotBlank(message = "{validation.jobCategory.name.notBlank}")
  @Size(max = 255, message = "{validation.jobCategory.name.size}")
  private String name;

  @NotBlank(message = "{validation.jobCategory.engName.notBlank}")
  @Size(max = 255, message = "{validation.jobCategory.engName.size}")
  private String engName;

  @Size(max = 1000, message = "{validation.jobCategory.description.size}")
  private String description;
}
