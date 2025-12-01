package beworkify.enumeration;

import beworkify.exception.AppException;

/**
 * Enumeration for age requirements in job postings.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
public enum AgeType {
  NONE("NONE"),
  ABOVE("ABOVE"),
  BELOW("BELOW"),
  INPUT("INPUT");

  private final String name;

  AgeType(String name) {
    this.name = name;
  }

  public static AgeType fromName(String name) {
    for (AgeType ageType : AgeType.values()) {
      if (ageType.name.equalsIgnoreCase(name)) {
        return ageType;
      }
    }
    throw new AppException(ErrorCode.INVALID_AGE_TYPE_ENUM);
  }
}
