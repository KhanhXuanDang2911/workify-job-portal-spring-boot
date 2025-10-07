package beworkify.enumeration;

import beworkify.exception.AppException;
import lombok.Getter;

@Getter
public enum JobLevel {
    INTERN("INTERN"), // Sinh viên / Thực tập sinh
    ENTRY_LEVEL("ENTRY_LEVEL"), // Mới đi làm
    STAFF("STAFF"), // Nhân viên
    ENGINEER("ENGINEER"), // Kỹ thuật viên / Kỹ sư
    SUPERVISOR("SUPERVISOR"), // Trưởng nhóm / Giám sát
    MANAGER("MANAGER"), // Quản lý / Trưởng phòng
    DIRECTOR("DIRECTOR"), // Giám đốc
    SENIOR_MANAGER("SENIOR_MANAGER"), // Quản lý cấp cao
    EXECUTIVE("EXECUTIVE"); // Điều hành cấp cao

    private final String value;

    JobLevel(String value) {
        this.value = value;
    }

    public static JobLevel fromValue(String value) {
        for (JobLevel level : JobLevel.values()) {
            if (level.getValue().equalsIgnoreCase(value)) {
                return level;
            }
        }
        throw new AppException(ErrorCode.INVALID_JOB_LEVEL_ENUM);
    }

}
