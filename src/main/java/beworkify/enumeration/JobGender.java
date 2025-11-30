package beworkify.enumeration;

import beworkify.exception.AppException;

public enum JobGender {
  MALE("MALE"),
  FEMALE("FEMALE"),
  ANY("ANY");

  private final String name;

  JobGender(String name) {
    this.name = name;
  }

  public static JobGender getGenderFromName(String name) {
    for (JobGender gender : JobGender.values()) {
      if (gender.name.equalsIgnoreCase(name)) return gender;
    }
    throw new AppException(ErrorCode.INVALID_JOB_GENDER_ENUM);
  }
}
