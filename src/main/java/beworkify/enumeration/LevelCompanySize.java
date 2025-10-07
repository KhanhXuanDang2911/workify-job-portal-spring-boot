package beworkify.enumeration;

import beworkify.exception.AppException;

public enum LevelCompanySize {
    LESS_THAN_10("LESS_THAN_10"),
    FROM_10_TO_24("FROM_10_TO_24"),
    FROM_25_TO_99("FROM_25_TO_99"),
    FROM_100_TO_499("FROM_100_TO_499"),
    FROM_500_TO_999("FROM_500_TO_999"),
    FROM_1000_TO_4999("FROM_1000_TO_4999"),
    FROM_5000_TO_9999("FROM_5000_TO_9999"),
    FROM_10000_TO_19999("FROM_10000_TO_19999"),
    FROM_20000_TO_49999("FROM_20000_TO_49999"),
    MORE_THAN_50000("MORE_THAN_50000");

    private final String label;

    LevelCompanySize(String label) {
        this.label = label;
    }

    public static LevelCompanySize fromLabel(String label) {
        for (LevelCompanySize s : LevelCompanySize.values()) {
            if (s.label.equals(label)) {
                return s;
            }
        }
        throw new AppException(ErrorCode.INVALID_LEVEL_COMPANY_SIZE_ENUM);
    }
}
