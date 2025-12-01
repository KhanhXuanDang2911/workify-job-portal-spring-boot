package beworkify.enumeration;

import beworkify.exception.AppException;
import lombok.Getter;

/**
 * Enumeration for job benefit types. Represents various perks and benefits offered by employers.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
@Getter
public enum BenefitType {
  TRAVEL_OPPORTUNITY("TRAVEL_OPPORTUNITY"),
  BONUS_GIFT("BONUS_GIFT"),
  SHUTTLE_BUS("SHUTTLE_BUS"),
  INSURANCE("INSURANCE"),
  LAPTOP_MONITOR("LAPTOP_MONITOR"),
  HEALTH_CARE("HEALTH_CARE"),
  PAID_LEAVE("PAID_LEAVE"),
  FLEXIBLE_REMOTE_WORK("FLEXIBLE_REMOTE_WORK"),
  SALARY_REVIEW("SALARY_REVIEW"),
  TEAM_BUILDING("TEAM_BUILDING"),
  TRAINING("TRAINING"),
  SNACKS_PANTRY("SNACKS_PANTRY"),
  WORK_ENVIRONMENT("WORK_ENVIRONMENT"),
  CHILD_CARE("CHILD_CARE"),
  OTHER("OTHER");

  private final String value;

  BenefitType(String value) {
    this.value = value;
  }

  public static BenefitType fromValue(String value) {
    for (BenefitType benefit : BenefitType.values()) {
      if (benefit.getValue().equalsIgnoreCase(value)) {
        return benefit;
      }
    }
    throw new AppException(ErrorCode.INVALID_JOB_BENEFIT_ENUM);
  }
}
