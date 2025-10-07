package beworkify.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignInRequest {
    @NotBlank(message = "Email must be not blank")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", message = "Email invalid format")
    private String email;
    @NotBlank(message = "Password must be not blank")
    @Size(min = 8, max = 160, message = "password must be between 8 and 160 characters")
    private String password;
}
