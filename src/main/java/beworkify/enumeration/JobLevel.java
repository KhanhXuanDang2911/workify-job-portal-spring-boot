
package beworkify.enumeration;

import beworkify.exception.AppException;
import lombok.Getter;

@Getter
public enum JobLevel {
	INTERN("INTERN"), ENTRY_LEVEL("ENTRY_LEVEL"), STAFF("STAFF"), ENGINEER("ENGINEER"), SUPERVISOR(
			"SUPERVISOR"), MANAGER(
					"MANAGER"), DIRECTOR("DIRECTOR"), SENIOR_MANAGER("SENIOR_MANAGER"), EXECUTIVE("EXECUTIVE");

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
