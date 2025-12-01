package beworkify.exception;

/**
 * Exception thrown when a resource conflict occurs (e.g., duplicate entry).
 *
 * @author KhanhDX
 * @since 1.0.0
 */
public class ResourceConflictException extends RuntimeException {
  public ResourceConflictException(String message) {
    super(message);
  }

  public ResourceConflictException(String message, Throwable cause) {
    super(message, cause);
  }
}
