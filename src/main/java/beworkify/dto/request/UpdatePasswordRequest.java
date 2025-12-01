package beworkify.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for updating user password. Requires current password for verification.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
@Getter
@Setter
public class UpdatePasswordRequest {
  @NotBlank(message = "{validation.password.not.blank}")
  @Pattern(
      regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[^A-Za-z0-9]).{8,160}$",
      message = "{validation.password.invalid}")
  private String currentPassword;

  @NotBlank(message = "{validation.password.not.blank}")
  @Pattern(
      regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[^A-Za-z0-9]).{8,160}$",
      message = "{validation.password.invalid}")
  private String newPassword;
}
