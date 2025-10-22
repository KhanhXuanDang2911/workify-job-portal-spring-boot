package beworkify.dto.request;

import jakarta.validation.constraints.Min;
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
public class IndustryRequest {

    @NotBlank(message = "{validation.industry.name.notBlank}")
    @Size(max = 255, message = "{validation.industry.name.size}")
    private String name;

    @NotBlank(message = "{validation.industry.engName.notBlank}")
    @Size(max = 255, message = "{validation.industry.engName.size}")
    private String engName;

    @Size(max = 1000, message = "{validation.industry.description.size}")
    private String description;

    @NotNull(message = "{validation.industry.jobCategoryId.not.null}")
    @Min(value = 1, message = "{validation.id.min}")
    private Long categoryJobId;
}
