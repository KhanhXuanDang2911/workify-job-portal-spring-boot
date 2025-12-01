package beworkify.exception;

import beworkify.enumeration.ErrorCode;
import lombok.Getter;

/**
 * Custom exception class for application-specific errors. Wraps an ErrorCode to provide detailed
 * error information.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
@Getter
public class AppException extends RuntimeException {

  private final ErrorCode errorCode;

  public AppException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }
}
