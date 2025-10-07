package beworkify.enumeration;

import beworkify.exception.AppException;
import lombok.Getter;

@Getter
public enum SalaryUnit {
    VND("VND"),
    USD("USD");

    private final String value;

    SalaryUnit(String value) {
        this.value = value;
    }

    public static SalaryUnit fromValue(String value) {
        for (SalaryUnit unit : SalaryUnit.values()) {
            if (unit.getValue().equalsIgnoreCase(value)) {
                return unit;
            }
        }
        throw new AppException(ErrorCode.INVALID_SALARY_UNIT_ENUM);
    }
}
