package beworkify.enumeration;

import beworkify.exception.AppException;
import lombok.Getter;

@Getter
public enum StatusUser {
    ACTIVE("ACTIVE"),
    PENDING("PENDING"),
    BANNED("BANNED");
    private final String name;

    StatusUser(String name){
        this.name = name;
    }
    public static StatusUser getStatusFromName(String name){
        for (StatusUser status : StatusUser.values()){
            if (status.name.equalsIgnoreCase(name))
                return status;
        }
        throw new AppException(ErrorCode.INVALID_STATUS_ENUM);
    }

}
