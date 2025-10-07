package beworkify.enumeration;

import beworkify.exception.AppException;

public enum AgeType {
    NONE("NONE"),
    ABOVE("ABOVE"),
    BELOW("BELOW"),
    INPUT("INPUT");

    private final String name;

    AgeType(String name) {
        this.name = name;
    }

    AgeType fromName(String name) {
        for (AgeType ageType : AgeType.values()) {
            if (ageType.name.equalsIgnoreCase(name)) {
                return ageType;
            }
        }
        throw new AppException(ErrorCode.INVALID_AGE_TYPE_ENUM);
    }
}