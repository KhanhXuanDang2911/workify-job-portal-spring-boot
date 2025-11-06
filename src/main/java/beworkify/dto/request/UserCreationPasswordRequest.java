package beworkify.dto.request;

import beworkify.validation.group.OnCreate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreationPasswordRequest {
    @NotBlank(message = "{validation.password.not.blank}", groups = { OnCreate.class })
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[^A-Za-z0-9]).{8,160}$", message = "{validation.password.invalid}", groups = {
            OnCreate.class })
    private String password;
}
