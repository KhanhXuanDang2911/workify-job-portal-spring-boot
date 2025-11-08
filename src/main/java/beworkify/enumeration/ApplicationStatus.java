
package beworkify.enumeration;

import beworkify.exception.AppException;
import lombok.Getter;

@Getter
public enum ApplicationStatus {
	UNREAD("UNREAD"), VIEWED("VIEWED"), EMAILED("EMAILED"), SCREENING("SCREENING"), SCREENING_PENDING(
			"SCREENING_PENDING"), INTERVIEW_SCHEDULING(
					"INTERVIEW_SCHEDULING"), INTERVIEWED_PENDING("INTERVIEWED_PENDING"), OFFERED("OFFERED"), // 📝 Đã
																												// mời
																												// nhận
																												// việc
	REJECTED("REJECTED");

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
