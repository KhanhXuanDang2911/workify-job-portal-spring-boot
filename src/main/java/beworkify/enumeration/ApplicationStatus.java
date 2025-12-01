package beworkify.enumeration;

import beworkify.exception.AppException;
import lombok.Getter;

/**
 * Enumeration for job application statuses. Tracks the progress of an application from submission
 * to decision.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
@Getter
public enum ApplicationStatus {
  UNREAD("UNREAD"),
  VIEWED("VIEWED"),
  EMAILED("EMAILED"),
  SCREENING("SCREENING"),
  SCREENING_PENDING("SCREENING_PENDING"),
  INTERVIEW_SCHEDULING("INTERVIEW_SCHEDULING"),
  INTERVIEWED_PENDING("INTERVIEWED_PENDING"),
  OFFERED("OFFERED"),
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
