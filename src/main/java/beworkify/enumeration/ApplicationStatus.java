package beworkify.enumeration;

import beworkify.exception.AppException;
import lombok.Getter;

import lombok.Getter;

@Getter
public enum ApplicationStatus {
    UNREAD("UNREAD"),                     // 📭 Chưa đọc
    VIEWED("VIEWED"),                     // 👀 Đã xem
    EMAILED("EMAILED"),                   // 📧 Đã gửi email
    SCREENING("SCREENING"),               // 🔍 Kiểm tra hồ sơ
    SCREENING_PENDING("SCREENING_PENDING"), // ⏳ Kiểm tra hồ sơ - Chưa quyết định
    INTERVIEW_SCHEDULING("INTERVIEW_SCHEDULING"), // 📅 Sắp xếp phỏng vấn
    INTERVIEWED_PENDING("INTERVIEWED_PENDING"),   // 💬 Đã phỏng vấn - Chưa quyết định
    OFFERED("OFFERED"),                   // 📝 Đã mời nhận việc
    REJECTED("REJECTED");                 // ❌ Từ chối

    private final String value;

    ApplicationStatus(String value) {
        this.value = value;
    }

    public static ApplicationStatus fromValue(String value) {
        for (ApplicationStatus status : ApplicationStatus.values()) {
            if (status.getValue().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new AppException(ErrorCode.INVALID_APPLICATION_STATUS_ENUM);
    }
}
