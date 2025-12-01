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
 * DTO for creating or updating a post category.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryPostRequest {

  @NotBlank(
      message = "{validation.categoryPost.title.notBlank}",
      groups = {OnCreate.class, OnUpdate.class})
  @Size(
      max = 255,
      message = "{validation.categoryPost.title.size}",
      groups = {OnCreate.class, OnUpdate.class})
  private String title;

  @Size(
      max = 1000,
      message = "{validation.categoryPost.description.size}",
      groups = {OnCreate.class, OnUpdate.class})
  private String description;
}
