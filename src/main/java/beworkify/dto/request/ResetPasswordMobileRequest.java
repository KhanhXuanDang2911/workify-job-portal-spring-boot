package beworkify.dto.request;

import beworkify.validation.group.OnCreate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordMobileRequest {
  @NotBlank(message = "{validation.verification.code.not.blank}")
  @Pattern(regexp = "^[0-9]{8}$", message = "{validation.code.invalid}")
  private String code;

  @NotBlank(
      message = "{validation.email.not.blank}",
      groups = {OnCreate.class})
  @Pattern(
      regexp =
          "^[a-zA-Z0-9](?:[a-zA-Z0-9._%+-]{0,63}[a-zA-Z0-9])?@[a-zA-Z0-9](?:[a-zA-Z0-9.-]{0,253}[a-zA-Z0-9])?\\.[a-zA-Z]{2,}$",
      message = "{validation.email.invalid}")
  private String email;

  @NotBlank(message = "{validation.password.not.blank}")
  @Pattern(
      regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[^A-Za-z0-9]).{8,160}$",
      message = "{validation.password.invalid}")
  private String newPassword;
}
