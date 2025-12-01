package beworkify.service;

import jakarta.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Service interface for email operations. Handles sending confirmation and password reset emails.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
public interface MailService {
  void sendConfirmLink(UserDetails recipient, boolean isMobile, String code)
      throws MessagingException, UnsupportedEncodingException;

  void sendResetLink(UserDetails recipient, boolean isMobile, String code)
      throws MessagingException, UnsupportedEncodingException;
}
