package beworkify.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignInRequest {
  @NotBlank(message = "Email must be not blank")
  @Pattern(
      regexp =
          "^[a-zA-Z0-9](?:[a-zA-Z0-9._%+-]{0,63}[a-zA-Z0-9])?@[a-zA-Z0-9](?:[a-zA-Z0-9.-]{0,253}[a-zA-Z0-9])?\\.[a-zA-Z]{2,}$",
      message = "{validation.email.invalid}")
  private String email;

  @NotBlank(message = "{validation.password.not.blank}")
  @Pattern(
      regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[^A-Za-z0-9]).{8,160}$",
      message = "{validation.password.invalid}")
  private String password;
}
