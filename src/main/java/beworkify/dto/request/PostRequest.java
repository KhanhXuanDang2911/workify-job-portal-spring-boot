package beworkify.dto.request;

import beworkify.enumeration.StatusPost;
import beworkify.validation.annotation.ValueOfEnum;
import beworkify.validation.group.OnCreate;
import beworkify.validation.group.OnUpdate;
import beworkify.validation.group.OnUserCreate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for creating or updating a blog post.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostRequest {

  @NotBlank(
      message = "{validation.post.title.notBlank}",
      groups = {OnCreate.class, OnUpdate.class, OnUserCreate.class})
  @Size(
      max = 255,
      message = "{validation.post.title.size}",
      groups = {OnCreate.class, OnUpdate.class, OnUserCreate.class})
  private String title;

  @NotBlank(
      message = "{validation.post.excerpt.notBlank}",
      groups = {OnCreate.class, OnUpdate.class, OnUserCreate.class})
  @Size(
      max = 1000,
      message = "{validation.post.excerpt.size}",
      groups = {OnCreate.class, OnUpdate.class, OnUserCreate.class})
  private String excerpt;

  @NotBlank(
      message = "{validation.post.content.notBlank}",
      groups = {OnCreate.class, OnUpdate.class, OnUserCreate.class})
  private String content;

  @NotNull(
      message = "{validation.post.category.required}",
      groups = {OnCreate.class, OnUpdate.class, OnUserCreate.class})
  private Long categoryId;

  @ValueOfEnum(
      enumClass = StatusPost.class,
      message = "{validation.post.status.invalid}",
      groups = {OnCreate.class, OnUpdate.class})
  private String status;
}
