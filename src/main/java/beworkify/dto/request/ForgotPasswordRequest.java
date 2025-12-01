package beworkify.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for forgot password request. Contains the email address to send the reset link/OTP.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
@Getter
@Setter
public class ForgotPasswordRequest {
  @NotBlank(message = "{validation.email.not.blank}")
  @Pattern(
      regexp =
          "^[a-zA-Z0-9](?:[a-zA-Z0-9._%+-]{0,63}[a-zA-Z0-9])?@[a-zA-Z0-9](?:[a-zA-Z0-9.-]{0,253}[a-zA-Z0-9])?\\.[a-zA-Z]{2,}$",
      message = "{validation.email.invalid}")
  private String email;
}
