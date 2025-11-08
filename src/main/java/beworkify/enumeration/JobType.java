
package beworkify.enumeration;

import beworkify.exception.AppException;
import lombok.Getter;

@Getter
public enum JobType {
	FULL_TIME("FULL_TIME"), TEMPORARY_FULL_TIME("TEMPORARY_FULL_TIME"), PART_TIME("PART_TIME"), TEMPORARY_PART_TIME(
			"TEMPORARY_PART_TIME"), CONTRACT("CONTRACT"), OTHER("OTHER");

	private final String value;

	JobType(String value) {
		this.value = value;
	}

	public static JobType fromValue(String value) {
		for (JobType type : JobType.values()) {
			if (type.getValue().equalsIgnoreCase(value)) {
				return type;
			}
		}
		throw new AppException(ErrorCode.INVALID_JOB_TYPE_ENUM);
	}
}
