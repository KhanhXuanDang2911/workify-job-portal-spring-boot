package beworkify.enumeration;

import beworkify.exception.AppException;
import lombok.Getter;

@Getter
public enum SalaryType {
    RANGE("RANGE"),
    GREATER_THAN("GREATER_THAN"),
    NEGOTIABLE("NEGOTIABLE"),
    COMPETITIVE("COMPETITIVE");

    private final String value;

    SalaryType(String value) {
        this.value = value;
    }

    SalaryType fromValue(String value) {
        for (SalaryType salaryType : SalaryType.values()) {
            if (salaryType.getValue().equals(value)) {
                return salaryType;
            }
        }
        throw new AppException(ErrorCode.INVALID_SALARY_TYPE_ENUM);
    }
}