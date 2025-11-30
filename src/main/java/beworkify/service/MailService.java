package beworkify.service;

import jakarta.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import org.springframework.security.core.userdetails.UserDetails;

public interface MailService {
  void sendConfirmLink(UserDetails recipient, boolean isMobile, String code)
      throws MessagingException, UnsupportedEncodingException;

  void sendResetLink(UserDetails recipient, boolean isMobile, String code)
      throws MessagingException, UnsupportedEncodingException;
}
