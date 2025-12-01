package beworkify.service.impl;

import beworkify.dto.request.ExchangeTokenGoogleRequest;
import beworkify.dto.request.SignInRequest;
import beworkify.dto.response.*;
import beworkify.entity.Employer;
import beworkify.entity.Role;
import beworkify.entity.User;
import beworkify.enumeration.*;
import beworkify.exception.InvalidTokenException;
import beworkify.exception.UnAuthorizeException;
import beworkify.mapper.EmployerMapper;
import beworkify.mapper.UserMapper;
import beworkify.repository.UserRepository;
import beworkify.repository.httpclient.GoogleIdentityClient;
import beworkify.repository.httpclient.GoogleUserInfoClient;
import beworkify.repository.httpclient.LinkedInIdentityClient;
import beworkify.repository.httpclient.LinkedInUserInfoClient;
import beworkify.service.*;
import beworkify.service.redis.RedisTokenService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the AuthenticationService interface. Handles user and employer authentication,
 * token management, and OAuth2 integration.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
@Service
@Transactional
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

  @Autowired
  @Qualifier("userAuthManager")
  private AuthenticationManager userAuthManager;

  @Autowired
  @Qualifier("employerAuthManager")
  private AuthenticationManager employerAuthManager;

  private final JwtService jwtService;
  private final UserService userService;
  private final GoogleIdentityClient googleIdentityClient;
  private final GoogleUserInfoClient googleUserInfoClient;
  private final LinkedInIdentityClient linkedInIdentityClient;
  private final LinkedInUserInfoClient linkedInUserInfoClient;
  private final UserRepository userRepository;
  private final RoleService roleService;
  private final MessageSource messageSource;
  private final UserMapper userMapper;
  private final EmployerMapper employerMapper;
  private final EmployerService employerService;
  private final RedisTokenService redisTokenService;

  @Value("${oauth2.google.client-id}")
  private String GOOGLE_CLIENT_ID;

  @Value("${oauth2.google.client-secret}")
  private String GOOGLE_CLIENT_SECRET;

  @Value("${oauth2.linkedin.client-id}")
  private String LINKEDIN_CLIENT_ID;

  @Value("${oauth2.linkedin.client-secret}")
  private String LINKEDIN_CLIENT_SECRET;

  @Value("${oauth2.linkedin.redirect-uri}")
  private String LINKEDIN_REDIRECT_URI;

  @Override
  public TokenResponse<UserResponse> userSignIn(SignInRequest request) {
    userAuthManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
    User user = userService.findUserByEmail(request.getEmail());
    if (user.getStatus().equals(StatusUser.PENDING)) {
      String message =
          messageSource.getMessage("error.pendingAccount", null, LocaleContextHolder.getLocale());
      throw new UnAuthorizeException(message);
    }
    String accessToken = jwtService.generateAccessToken(user, AccountType.USER);
    String refreshToken = jwtService.generateRefreshToken(user, AccountType.USER);

    redisTokenService.saveAccessToken(accessToken);
    redisTokenService.saveRefreshToken(refreshToken);

    UserResponse userResponse = userMapper.toDTO(user);
    userResponse.setRole(user.getRole().getRole());

    return TokenResponse.<UserResponse>builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .data(userResponse)
        .build();
  }

  @Override
  public TokenResponse<EmployerResponse> employerSignIn(SignInRequest request) {
    employerAuthManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
    Employer employer = employerService.findEmployerByEmail(request.getEmail());
    if (employer.getStatus().equals(StatusUser.PENDING)) {
      String message =
          messageSource.getMessage("error.pendingAccount", null, LocaleContextHolder.getLocale());
      throw new UnAuthorizeException(message);
    }
    String accessToken = jwtService.generateAccessToken(employer, AccountType.EMPLOYER);
    String refreshToken = jwtService.generateRefreshToken(employer, AccountType.EMPLOYER);

    redisTokenService.saveAccessToken(accessToken);
    redisTokenService.saveRefreshToken(refreshToken);

    EmployerResponse employerResponse = employerMapper.toDTO(employer);

    return TokenResponse.<EmployerResponse>builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .data(employerResponse)
        .build();
  }

  @Override
  public TokenResponse<UserResponse> authenticateGoogle(String code) {
    String GRANT_TYPE = "authorization_code";
    String REDIRECT_URI = "postmessage";
    ExchangeTokenGoogleRequest request =
        ExchangeTokenGoogleRequest.builder()
            .code(code)
            .redirectUri(REDIRECT_URI)
            .clientId(GOOGLE_CLIENT_ID)
            .clientSecret(GOOGLE_CLIENT_SECRET)
            .grantType(GRANT_TYPE)
            .build();
    ExchangeTokenGoogleResponse exchangeTokenResponse = googleIdentityClient.getToken(request);
    GoogleUserInfoResponse userInfoResponse =
        googleUserInfoClient.getUserInfo("Bearer " + exchangeTokenResponse.getAccessToken());

    if (!userRepository.existsByEmail(userInfoResponse.getEmail())) {
      Role role = roleService.findRoleByRoleName(UserRole.JOB_SEEKER.getName());
      User user =
          User.builder()
              .email(userInfoResponse.getEmail())
              .fullName(userInfoResponse.getName())
              .avatarUrl(userInfoResponse.getPicture())
              .status(StatusUser.ACTIVE)
              .role(role)
              .gender(Gender.MALE)
              .noPassword(true)
              .build();
      User userResponse = userRepository.save(user);
      String createdPasswordToken =
          jwtService.generateToken(userResponse, TokenType.CREATE_PASSWORD_TOKEN, 1);

      return TokenResponse.<UserResponse>builder()
          .accessToken(null)
          .refreshToken(null)
          .data(null)
          .createPasswordToken(createdPasswordToken)
          .build();
    } else {
      User userResponse = userService.findUserByEmail(userInfoResponse.getEmail());
      if (userResponse.getNoPassword()) {
        String createdPasswordToken =
            jwtService.generateToken(userResponse, TokenType.CREATE_PASSWORD_TOKEN, 1);
        return TokenResponse.<UserResponse>builder()
            .accessToken(null)
            .refreshToken(null)
            .data(null)
            .createPasswordToken(createdPasswordToken)
            .build();
      }
      String accessToken = jwtService.generateAccessToken(userResponse, AccountType.USER);
      String refreshToken = jwtService.generateRefreshToken(userResponse, AccountType.USER);
      redisTokenService.saveAccessToken(accessToken);
      redisTokenService.saveRefreshToken(refreshToken);

      if (userResponse.getStatus().equals(StatusUser.PENDING)) {
        userResponse.setStatus(StatusUser.ACTIVE);
      }
      UserResponse data = userMapper.toDTO(userResponse);
      data.setRole(userResponse.getRole().getRole());

      return TokenResponse.<UserResponse>builder()
          .accessToken(accessToken)
          .refreshToken(refreshToken)
          .data(data)
          .createPasswordToken(null)
          .build();
    }
  }

  @Override
  public TokenResponse<Void> refreshTokenUser(String refreshToken) {
    if (StringUtils.isBlank(refreshToken)) {
      throw new InvalidTokenException();
    }
    String email = jwtService.extractEmail(refreshToken, TokenType.REFRESH_TOKEN);

    UserDetails user = userService.findUserByEmail(email);
    if (!jwtService.isTokenValid(refreshToken, user, TokenType.REFRESH_TOKEN)
        || !redisTokenService.existsByJwtId(refreshToken, TokenType.REFRESH_TOKEN)) {
      throw new InvalidTokenException();
    }
    String newAccessToken = jwtService.generateAccessToken(user, AccountType.USER);

    redisTokenService.saveAccessToken(newAccessToken);

    return TokenResponse.<Void>builder()
        .accessToken(newAccessToken)
        .refreshToken(refreshToken)
        .build();
  }

  @Override
  public TokenResponse<Void> refreshTokenEmployer(String refreshToken) {
    if (StringUtils.isBlank(refreshToken)) {
      throw new InvalidTokenException();
    }
    String email = jwtService.extractEmail(refreshToken, TokenType.REFRESH_TOKEN);

    UserDetails user = employerService.findEmployerByEmail(email);
    if (!jwtService.isTokenValid(refreshToken, user, TokenType.REFRESH_TOKEN)
        || !redisTokenService.existsByJwtId(refreshToken, TokenType.REFRESH_TOKEN)) {
      throw new InvalidTokenException();
    }
    String newAccessToken = jwtService.generateAccessToken(user, AccountType.EMPLOYER);

    redisTokenService.saveAccessToken(newAccessToken);

    return TokenResponse.<Void>builder()
        .accessToken(newAccessToken)
        .refreshToken(refreshToken)
        .build();
  }

  @Override
  public void signOut(String accessToken, String refreshToken) {
    if (StringUtils.isNotBlank(accessToken)
        && redisTokenService.existsByJwtId(accessToken, TokenType.ACCESS_TOKEN)) {
      redisTokenService.deleteByJwtId(accessToken, TokenType.ACCESS_TOKEN);
    }
    if (StringUtils.isNotBlank(refreshToken)
        && redisTokenService.existsByJwtId(refreshToken, TokenType.REFRESH_TOKEN)) {
      redisTokenService.deleteByJwtId(refreshToken, TokenType.REFRESH_TOKEN);
    }
  }

  @Override
  public TokenResponse<UserResponse> authenticateLinkedIn(String code) {
    String GRANT_TYPE = "authorization_code";
    ExchangeTokenLinkedInResponse exchangeTokenResponse =
        linkedInIdentityClient.getToken(
            GRANT_TYPE, code, LINKEDIN_REDIRECT_URI, LINKEDIN_CLIENT_ID, LINKEDIN_CLIENT_SECRET);
    LinkedInUserInfoResponse userInfoResponse =
        linkedInUserInfoClient.getUserInfo("Bearer " + exchangeTokenResponse.getAccessToken());
    if (!userRepository.existsByEmail(userInfoResponse.getEmail())) {
      Role role = roleService.findRoleByRoleName(UserRole.JOB_SEEKER.getName());
      User user =
          User.builder()
              .email(userInfoResponse.getEmail())
              .fullName(userInfoResponse.getName())
              .avatarUrl(userInfoResponse.getPicture())
              .status(StatusUser.ACTIVE)
              .role(role)
              .gender(Gender.MALE)
              .noPassword(true)
              .build();
      User userResponse = userRepository.save(user);
      String createdPasswordToken =
          jwtService.generateToken(userResponse, TokenType.CREATE_PASSWORD_TOKEN, 1);
      UserResponse data = userMapper.toDTO(userResponse);
      data.setRole(userResponse.getRole().getRole());
      return TokenResponse.<UserResponse>builder()
          .accessToken(null)
          .refreshToken(null)
          .data(data)
          .createPasswordToken(createdPasswordToken)
          .build();
    } else {
      User userResponse = userService.findUserByEmail(userInfoResponse.getEmail());
      if (userResponse.getNoPassword()) {
        String createdPasswordToken =
            jwtService.generateToken(userResponse, TokenType.CREATE_PASSWORD_TOKEN, 1);
        UserResponse data = userMapper.toDTO(userResponse);
        data.setRole(userResponse.getRole().getRole());
        return TokenResponse.<UserResponse>builder()
            .accessToken(null)
            .refreshToken(null)
            .data(data)
            .createPasswordToken(createdPasswordToken)
            .build();
      }
      String accessToken = jwtService.generateAccessToken(userResponse, AccountType.USER);
      String refreshToken = jwtService.generateRefreshToken(userResponse, AccountType.USER);
      redisTokenService.saveAccessToken(accessToken);
      redisTokenService.saveRefreshToken(refreshToken);
      if (userResponse.getStatus().equals(StatusUser.PENDING)) {
        userResponse.setStatus(StatusUser.ACTIVE);
      }
      UserResponse data = userMapper.toDTO(userResponse);
      data.setRole(userResponse.getRole().getRole());

      return TokenResponse.<UserResponse>builder()
          .accessToken(accessToken)
          .refreshToken(refreshToken)
          .data(data)
          .createPasswordToken(null)
          .build();
    }
  }
}
