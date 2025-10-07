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
public class CategoryPostRequest {

    @NotBlank(message = "{validation.categoryPost.title.notBlank}", groups = { OnCreate.class, OnUpdate.class })
    @Size(max = 255, message = "{validation.categoryPost.title.size}", groups = { OnCreate.class, OnUpdate.class })
    private String title;

    @Size(max = 1000, message = "{validation.categoryPost.description.size}", groups = { OnCreate.class,
            OnUpdate.class })
    private String description;
}
