package beworkify.enumeration;

import beworkify.exception.AppException;
import lombok.Getter;

@Getter
public enum JobStatus {
    DRAFT("DRAFT"),
    PENDING("PENDING"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED"),
    CLOSED("CLOSED"),
    EXPIRED("EXPIRED");

    private final String value;

    JobStatus(String value) {
        this.value = value;
    }

    public static JobStatus fromValue(String value) {
        for (JobStatus status : JobStatus.values()) {
            if (status.getValue().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new AppException(ErrorCode.INVALID_JOB_STATUS_ENUM);
    }
}
