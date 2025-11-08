
package beworkify.service.impl;

import beworkify.entity.Employer;
import beworkify.entity.User;
import beworkify.enumeration.TokenType;
import beworkify.service.JwtService;
import beworkify.service.MailService;
import beworkify.service.redis.RedisTokenService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {
	private final JavaMailSender javaMailSender;
	private final TemplateEngine templateEngine;
	private final JwtService jwtService;
	private final RedisTokenService redisTokenService;

	@Value("${spring.mail.from}")
	private String emailFrom;

	@Override
	@Async
	public void sendConfirmLink(UserDetails recipient, boolean isMobile, String code)
			throws MessagingException, UnsupportedEncodingException {
		String userEmail = null;
		String fullName = null;

		try {
			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

			Context context = new Context();
			Map<String, Object> properties = new HashMap<>();

			if (recipient instanceof User) {
				userEmail = ((User) recipient).getEmail();
				fullName = ((User) recipient).getFullName();
				if (isMobile) {
					properties.put("otpCode", code);
				} else {
					String confirmToken = jwtService.generateToken(recipient, TokenType.CONFIRM_TOKEN, 24);
					properties.put("confirmationUrl",
							String.format("http://localhost:5173/verify-email?token=%s", confirmToken));
				}
			} else if (recipient instanceof Employer) {
				userEmail = ((Employer) recipient).getEmail();
				fullName = ((Employer) recipient).getCompanyName();
				if (isMobile) {
					properties.put("otpCode", code);
				} else {
					String confirmToken = jwtService.generateToken(recipient, TokenType.CONFIRM_TOKEN, 24);
					properties.put("confirmationUrl",
							String.format("http://localhost:5173/employer/verify-email?token=%s", confirmToken));
				}
			}
			properties.put("fullName", fullName);
			properties.put("userEmail", userEmail);
			context.setVariables(properties);
			String html;
			if (isMobile) {
				html = templateEngine.process("confirm-email-mobile", context);
			} else {
				html = templateEngine.process("confirm-email", context);
			}

			helper.setFrom(emailFrom, "Workify Platform");
			helper.setTo(userEmail);
			helper.setSubject("Confirm email");
			helper.setText(html, true);

			try {
				Resource logo = new ClassPathResource("templates/logo.png");
				if (logo.exists()) {
					helper.addInline("logo", logo);
				} else {
					log.warn("Logo resource not found on classpath: templates/logo.png");
				}
			} catch (Exception ex) {
				log.warn("Failed to attach inline logo: {}", ex.getMessage());
			}

			javaMailSender.send(message);

			log.info("Confirmation email sent successfully to {}", userEmail);
		} catch (Exception e) {
			log.error("Failed to send confirmation email to {}: {}", userEmail, e.getMessage(), e);
			throw e;
		}
	}

	@Override
	@Async
	public void sendResetLink(UserDetails recipient, boolean isMobile, String code)
			throws MessagingException, UnsupportedEncodingException {
		String userEmail = null;
		String fullName = null;
		try {
			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

			Context context = new Context();
			Map<String, Object> properties = new HashMap<>();

			if (recipient instanceof User) {
				userEmail = ((User) recipient).getEmail();
				fullName = ((User) recipient).getFullName();
				if (isMobile) {
					properties.put("otpCode", code);
				} else {
					String resetToken = jwtService.generateToken(recipient, TokenType.RESET_TOKEN, 1);
					redisTokenService.saveResetToken(resetToken);
					properties.put("resetUrl",
							String.format("http://localhost:5173/reset-password?token=%s", resetToken));
				}
			} else if (recipient instanceof Employer) {
				userEmail = ((Employer) recipient).getEmail();
				fullName = ((Employer) recipient).getCompanyName();
				if (isMobile) {
					properties.put("otpCode", code);
				} else {
					String resetToken = jwtService.generateToken(recipient, TokenType.RESET_TOKEN, 1);
					redisTokenService.saveResetToken(resetToken);
					properties.put("resetUrl",
							String.format("http://localhost:5173/employer/reset-password?token=%s", resetToken));
				}
			}

			properties.put("userEmail", userEmail);
			properties.put("fullName", fullName);
			context.setVariables(properties);

			String html;
			if (isMobile) {
				html = templateEngine.process("reset-password-mobile", context);
			} else {
				html = templateEngine.process("reset-password", context);
			}

			helper.setFrom(emailFrom, "Workify Platform");
			helper.setTo(userEmail);
			helper.setSubject("Reset password email");
			helper.setText(html, true);

			try {
				Resource logo = new ClassPathResource("templates/logo.png");
				if (logo.exists()) {
					helper.addInline("logo", logo);
				} else {
					log.warn("Logo resource not found on classpath: templates/logo.png");
				}
			} catch (Exception ex) {
				log.warn("Failed to attach inline logo to reset email: {}", ex.getMessage());
			}

			javaMailSender.send(message);
			log.info("Reset url sent successfully to {}", userEmail);
		} catch (Exception e) {
			log.error("Failed to send reset link to email {}: {}", userEmail, e.getMessage(), e);
			throw e;
		}
	}
}
