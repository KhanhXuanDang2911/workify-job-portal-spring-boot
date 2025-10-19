package beworkify.dto.request;

import beworkify.validation.OnCreate;
import beworkify.validation.OnUpdate;
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
public class IndustryRequest {

    @NotBlank(message = "{validation.industry.name.notBlank}", groups = { OnCreate.class, OnUpdate.class })
    @Size(max = 255, message = "{validation.industry.name.size}", groups = { OnCreate.class, OnUpdate.class })
    private String name;

    @NotBlank(message = "{validation.industry.engName.notBlank}", groups = { OnCreate.class, OnUpdate.class })
    @Size(max = 255, message = "{validation.industry.engName.size}", groups = { OnCreate.class, OnUpdate.class })
    private String engName;

    @Size(max = 1000, message = "{validation.industry.description.size}", groups = { OnCreate.class,
            OnUpdate.class })
    private String description;
}
