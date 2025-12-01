package beworkify.exception;

/**
 * Exception thrown when an invalid or expired token is encountered.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
public class InvalidTokenException extends RuntimeException {
  public InvalidTokenException() {}

  public InvalidTokenException(String msg) {
    super(msg);
  }

  public InvalidTokenException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
