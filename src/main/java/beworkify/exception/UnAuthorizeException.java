package beworkify.exception;

/**
 * Exception thrown when a user is not authorized to access a resource.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
public class UnAuthorizeException extends RuntimeException {
  public UnAuthorizeException(String message) {
    super(message);
  }
}
