package beworkify.enumeration;

import beworkify.exception.AppException;
import lombok.Getter;

/**
 * Enumeration for experience levels required for a job.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
@Getter
public enum ExperienceLevel {
  LESS_THAN_ONE_YEAR("LESS_THAN_ONE_YEAR"),
  ONE_TO_TWO_YEARS("ONE_TO_TWO_YEARS"),
  TWO_TO_FIVE_YEARS("TWO_TO_FIVE_YEARS"),
  FIVE_TO_TEN_YEARS("FIVE_TO_TEN_YEARS"),
  MORE_THAN_TEN_YEARS("MORE_THAN_TEN_YEARS");

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
