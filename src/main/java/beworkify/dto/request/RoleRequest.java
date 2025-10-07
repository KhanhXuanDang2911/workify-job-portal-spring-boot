package beworkify.dto.request;

import beworkify.enumeration.UserRole;
import beworkify.validation.ValueOfEnum;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleRequest {
    @ValueOfEnum(enumClass = UserRole.class, message = "Role must be one of: USER, EMPLOYER, ADMIN")
    private String role;
    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;
}
