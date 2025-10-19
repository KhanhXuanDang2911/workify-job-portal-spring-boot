package beworkify.service;

import jakarta.mail.MessagingException;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.UnsupportedEncodingException;

public interface MailService {
    void sendConfirmLink(UserDetails recipient) throws MessagingException, UnsupportedEncodingException;

    void sendResetLink(UserDetails recipient) throws MessagingException, UnsupportedEncodingException;

}
