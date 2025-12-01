package beworkify.enumeration;

import beworkify.exception.AppException;
import lombok.Getter;

/**
 * Enumeration for education levels required for a job.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
@Getter
public enum EducationLevel {
  HIGH_SCHOOL("HIGH_SCHOOL"),
  COLLEGE("COLLEGE"),
  UNIVERSITY("UNIVERSITY"),
  POSTGRADUATE("POSTGRADUATE"),
  MASTER("MASTER"),
  DOCTORATE("DOCTORATE"),
  OTHER("OTHER");

  private final String value;

  EducationLevel(String value) {
    this.value = value;
  }

  public static EducationLevel fromValue(String value) {
    for (EducationLevel level : EducationLevel.values()) {
      if (level.getValue().equalsIgnoreCase(value)) {
        return level;
      }
    }
    throw new AppException(ErrorCode.INVALID_EDUCATION_LEVEL_ENUM);
  }
}
