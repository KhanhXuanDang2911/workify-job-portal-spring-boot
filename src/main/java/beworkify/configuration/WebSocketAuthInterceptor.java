package beworkify.configuration;

import beworkify.enumeration.TokenType;
import beworkify.service.JwtService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

/**
 * Interceptor for WebSocket authentication. Validates JWT tokens in STOMP headers during connection
 * establishment.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor {

  private final JwtService jwtService;

  @Qualifier("userDetailsService")
  private final UserDetailsService userDetailsService;

  @Qualifier("employerDetailsService")
  private final UserDetailsService employerDetailsService;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor =
        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

    if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
      String token = extractToken(accessor);

      if (token != null) {
        try {
          String email = jwtService.extractEmail(token, TokenType.ACCESS_TOKEN);
          String accountType = jwtService.extractAccountType(token, TokenType.ACCESS_TOKEN);

          UserDetailsService service =
              "EMPLOYER".equalsIgnoreCase(accountType)
                  ? employerDetailsService
                  : userDetailsService;
          UserDetails userDetails = service.loadUserByUsername(email);

          if (jwtService.isTokenValid(token, userDetails, TokenType.ACCESS_TOKEN)) {
            String principalName =
                ("EMPLOYER".equalsIgnoreCase(accountType) ? "EMPLOYER:" : "USER:") + email;

            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                    new org.springframework.security.core.userdetails.User(
                        principalName, userDetails.getPassword(), userDetails.getAuthorities()),
                    null,
                    userDetails.getAuthorities());

            accessor.setUser(authentication);
            log.debug("WebSocket authenticated: {} ({})", principalName, accountType);
          }
        } catch (Exception e) {
          log.error("Failed to authenticate WebSocket connection", e);
        }
      }
    }

    return message;
  }

  private String extractToken(StompHeaderAccessor accessor) {
    List<String> authHeaders = accessor.getNativeHeader("Authorization");
    if (authHeaders != null && !authHeaders.isEmpty()) {
      String authHeader = authHeaders.get(0);
      if (authHeader.startsWith("Bearer ")) {
        return authHeader.substring(7);
      }
    }

    List<String> tokenParams = accessor.getNativeHeader("token");
    if (tokenParams != null && !tokenParams.isEmpty()) {
      return tokenParams.get(0);
    }

    return null;
  }
}
