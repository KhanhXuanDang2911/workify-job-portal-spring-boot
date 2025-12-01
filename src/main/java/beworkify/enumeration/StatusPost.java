package beworkify.enumeration;

import beworkify.exception.AppException;
import lombok.Getter;

/**
 * Enumeration for blog post status.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
@Getter
public enum StatusPost {
  PENDING("PENDING"),
  PUBLIC("PUBLIC"),
  DRAFT("DRAFT");

  private final String value;

  StatusPost(String value) {
    this.value = value;
  }

  public static StatusPost fromValue(String value) {
    if (value == null) return null;
    for (StatusPost post : StatusPost.values()) {
      if (post.value.equalsIgnoreCase(value)) {
        return post;
      }
    }
    throw new AppException(ErrorCode.INVALID_STATUS_POST_ENUM);
  }
}
