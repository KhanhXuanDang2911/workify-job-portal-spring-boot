package beworkify.enumeration;

import beworkify.exception.AppException;
import lombok.Getter;

@Getter
public enum UserRole {
    JOB_SEEKER("JOB_SEEKER"),
    ADMIN("ADMIN");

    private final String name;

    UserRole(String name){
        this.name = name;
    }

    public static UserRole getRoleFromName(String name){
        for (UserRole role : UserRole.values()){
            if (role.name.equalsIgnoreCase(name))
                return role;
        }
        throw new AppException(ErrorCode.INVALID_ROLE_ENUM);
    }
}
