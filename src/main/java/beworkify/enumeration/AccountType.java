package beworkify.enumeration;

import lombok.Getter;

/**
 * Enumeration for account types (USER, EMPLOYER).
 *
 * @author KhanhDX
 * @since 1.0.0
 */
@Getter
public enum AccountType {
  USER("USER"),
  EMPLOYER("EMPLOYER");

  private final String type;

  AccountType(String type) {
    this.type = type;
  }
}
