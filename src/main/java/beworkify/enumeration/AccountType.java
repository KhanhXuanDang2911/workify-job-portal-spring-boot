package beworkify.enumeration;

import lombok.Getter;

@Getter
public enum AccountType {
  USER("USER"),
  EMPLOYER("EMPLOYER");

  private final String type;

  AccountType(String type) {
    this.type = type;
  }
}
