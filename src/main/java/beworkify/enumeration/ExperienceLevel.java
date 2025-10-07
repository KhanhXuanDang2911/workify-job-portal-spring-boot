package beworkify.enumeration;

import beworkify.exception.AppException;
import lombok.Getter;

@Getter
public enum ExperienceLevel {
    LESS_THAN_ONE_YEAR("LESS_THAN_ONE_YEAR"), // 0 - 1 năm
    ONE_TO_TWO_YEARS("ONE_TO_TWO_YEARS"), // 1 - 2 năm
    TWO_TO_FIVE_YEARS("TWO_TO_FIVE_YEARS"), // 2 - 5 năm
    FIVE_TO_TEN_YEARS("FIVE_TO_TEN_YEARS"), // 5 - 10 năm
    MORE_THAN_TEN_YEARS("MORE_THAN_TEN_YEARS"); // Hơn 10 năm

    private final String value;

    ExperienceLevel(String value) {
        this.value = value;
    }

    public static ExperienceLevel fromValue(String value) {
        for (ExperienceLevel level : ExperienceLevel.values()) {
            if (level.getValue().equalsIgnoreCase(value)) {
                return level;
            }
        }
        throw new AppException(ErrorCode.INVALID_EXPERIENCE_LEVEL_ENUM);
    }

}
