package beworkify.enumeration;

import beworkify.exception.AppException;

/**
 * Enumeration for user gender.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
public enum Gender {
  MALE("MALE"),
  FEMALE("FEMALE"),
  OTHER("OTHER");

  private final String name;

  Gender(String name) {
    this.name = name;
  }

  public static Gender getGenderFromName(String name) {
    for (Gender gender : Gender.values()) {
      if (gender.name.equalsIgnoreCase(name)) return gender;
    }
    throw new AppException(ErrorCode.INVALID_GENDER_ENUM);
  }
}
