package beworkify.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import beworkify.enumeration.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoleResponse extends BaseResponse {
    private UserRole role;
    private String description;
}
