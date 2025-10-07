package beworkify.dto.request;

import beworkify.enumeration.StatusPost;
import beworkify.validation.OnCreate;
import beworkify.validation.OnUpdate;
import beworkify.validation.OnUserCreate;
import beworkify.validation.ValueOfEnum;
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
public class PostRequest {

        @NotBlank(message = "{validation.post.title.notBlank}", groups = { OnCreate.class, OnUpdate.class,
                        OnUserCreate.class })
        @Size(max = 255, message = "{validation.post.title.size}", groups = { OnCreate.class, OnUpdate.class,
                        OnUserCreate.class })
        private String title;

        @NotBlank(message = "{validation.post.excerpt.notBlank}", groups = { OnCreate.class, OnUpdate.class,
                        OnUserCreate.class })
        @Size(max = 1000, message = "{validation.post.excerpt.size}", groups = { OnCreate.class, OnUpdate.class,
                        OnUserCreate.class })
        private String excerpt;

        @NotBlank(message = "{validation.post.content.notBlank}", groups = { OnCreate.class, OnUpdate.class,
                        OnUserCreate.class })
        private String content;

        @NotNull(message = "{validation.post.category.required}", groups = { OnCreate.class, OnUpdate.class,
                        OnUserCreate.class })
        private Long categoryId;

        @ValueOfEnum(enumClass = StatusPost.class, message = "{validation.post.status.invalid}", groups = {
                        OnCreate.class, OnUpdate.class })
        private String status;
}