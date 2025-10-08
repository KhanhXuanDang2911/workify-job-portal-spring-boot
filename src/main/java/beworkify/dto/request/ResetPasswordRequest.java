package beworkify.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {
    @NotBlank(message = "{validation.password.not.blank}")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[^A-Za-z0-9]).{8,160}$", message = "{validation.password.invalid}")
    private String newPassword;
}
