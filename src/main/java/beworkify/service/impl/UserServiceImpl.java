package beworkify.service.impl;

import beworkify.dto.request.*;
import beworkify.dto.response.PageResponse;
import beworkify.dto.response.TokenResponse;
import beworkify.dto.response.UserResponse;
import beworkify.entity.*;
import beworkify.enumeration.*;
import beworkify.exception.AppException;
import beworkify.exception.InvalidTokenException;
import beworkify.exception.ResourceConflictException;
import beworkify.exception.ResourceNotFoundException;
import beworkify.mapper.UserMapper;
import beworkify.repository.UserRepository;
import beworkify.service.*;
import beworkify.util.AppUtils;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final MessageSource messageSource;
    private final AzureBlobService azureBlobService;
    private final JwtService jwtService;
    private final MailService mailService;
    private final WhitelistTokenService whitelistTokenService;
    private final ProvinceService provinceService;
    private final DistrictService districtService;

    @Override
    public UserResponse getUserById(Long id) {
        User user = findUserById(id);
        UserResponse response = userMapper.toDTO(user);
        if (user.getRole() != null)
            response.setRole(user.getRole().getRole());
        return response;
    }

    @Transactional(readOnly = true)
    @Override
    public PageResponse<List<UserResponse>> getUsersWithPaginationAndKeywordAndSorts(int pageNumber, int pageSize,
            List<String> sorts, String keyword) {
        log.info("Fetching users with pagination: pageNumber={}, pageSize={}", pageNumber, pageSize);

        List<String> whiteListFieldSorts = List.of("fullName", "email", "phoneNumber", "birthDate", "gender",  "role", "status", "createdAt", "updatedAt");
        Page<User> userPage = userRepository.searchUsers(keyword.toLowerCase(),
                AppUtils.generatePageableWithSort(sorts, whiteListFieldSorts, pageNumber, pageSize));

        List<UserResponse> userResponses = userPage.getContent()
                .stream()
                .map(user -> {
                    UserResponse userResponse = userMapper.toDTO(user);
                    userResponse.setRole(user.getRole().getRole());
                    return userResponse;
                })
                .toList();

        log.info("Fetched {} users (page {}/{})",
                userPage.getNumberOfElements(), pageNumber, userPage.getTotalPages());

        return PageResponse.<List<UserResponse>>builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .totalPages(userPage.getTotalPages())
                .numberOfElements(userPage.getNumberOfElements())
                .items(userResponses)
                .build();
    }

    @Override
    public UserResponse createUser(UserRequest request, MultipartFile avatar) {
        log.info("Admin creating user with email {}", request.getEmail());
        if (userRepository.existsByEmail(request.getEmail())) {
            String message = messageSource.getMessage("user.email.exists", new Object[] { request.getEmail() },
                    LocaleContextHolder.getLocale());
            throw new ResourceConflictException(message);
        }

        User user = userMapper.toEntity(request);
        if (request.getProvinceId() != null) {
            Province province = provinceService.findProvinceById(request.getProvinceId());
            user.setProvince(province);
        }
        if (request.getDistrictId() != null) {
            District district = districtService.findDistrictById(request.getDistrictId());
            user.setDistrict(district);
        }

        if (avatar != null && !avatar.isEmpty()) {
            String avatarUrl = azureBlobService.uploadFile(avatar);
            user.setAvatarUrl(avatarUrl);
        }

        Role role = roleService.findRoleByRoleName(request.getRole());
        user.setRole(role);
        user.setStatus(StatusUser.getStatusFromName(request.getStatus()));
        if (request.getGender() != null) {
            user.setGender(Gender.getGenderFromName(request.getGender()));
        }
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNoPassword(false);
        userRepository.save(user);
        UserResponse response = userMapper.toDTO(user);
        response.setRole(user.getRole().getRole());

        log.info("Admin create user successfully with id = {}", user.getId());
        return response;
    }

    @Override
    public UserResponse updateUser(UserRequest request, MultipartFile avatar, Long id) {
        log.info("Admin updating user with email {}", request.getEmail());
        User user = findUserById(id);
        userMapper.updateEntityFromDTO(request, user);
        if (userRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
            String message = messageSource.getMessage("user.email.exists", new Object[] { request.getEmail() },
                    LocaleContextHolder.getLocale());
            throw new ResourceConflictException(message);
        }

        if (request.getProvinceId() != null) {
            Province province = provinceService.findProvinceById(request.getProvinceId());
            user.setProvince(province);
        }
        if (request.getDistrictId() != null) {
            District district = districtService.findDistrictById(request.getDistrictId());
            user.setDistrict(district);
        }

        if (avatar != null && !avatar.isEmpty()) {
            String avatarUrl = azureBlobService.uploadFile(avatar);
            user.setAvatarUrl(avatarUrl);
        }

        Role role = roleService.findRoleByRoleName(request.getRole());
        user.setRole(role);
        user.setEmail(request.getEmail());
        user.setStatus(StatusUser.getStatusFromName(request.getStatus()));
        if (request.getGender() != null) {
            user.setGender(Gender.getGenderFromName(request.getGender()));
        }
        if (request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        user.setNoPassword(false);
        userRepository.save(user);
        UserResponse response = userMapper.toDTO(user);
        response.setRole(user.getRole().getRole());

        log.info("Admin updating user successfully with id = {}", user.getId());
        return response;
    }

    @Override
    public UserResponse updateProfile(Long userId, UserRequest request) {
        log.info("User with email {} updating profile", request.getEmail());
        User user = findUserById(userId);
        userMapper.updateEntityFromDTO(request, user);

        if (request.getProvinceId() != null) {
            Province province = provinceService.findProvinceById(request.getProvinceId());
            user.setProvince(province);
        }
        if (request.getDistrictId() != null) {
            District district = districtService.findDistrictById(request.getDistrictId());
            user.setDistrict(district);
        }
        if (request.getGender() != null) {
            user.setGender(Gender.getGenderFromName(request.getGender()));
        }
        userRepository.save(user);
        UserResponse response = userMapper.toDTO(user);
        response.setRole(user.getRole().getRole());

        log.info("User with email {} updated profile successfully", response.getEmail());
        return response;
    }

    @Override
    public UserResponse updateAvatar(Long userId, MultipartFile avatar) {
        User user = findUserById(userId);
        if (avatar != null && !avatar.isEmpty()) {
            String avatarUrl = azureBlobService.uploadFile(avatar);
            user.setAvatarUrl(avatarUrl);
        }
        userRepository.save(user);
        UserResponse response = userMapper.toDTO(user);
        response.setRole(user.getRole().getRole());
        return response;
    }

    @Override
    public void verifyEmailUser(String confirmToken) {
        String email = jwtService.extractEmail(confirmToken, TokenType.CONFIRM_TOKEN);
        User user = findUserByEmail(email);
        if (!user.getStatus().equals(StatusUser.PENDING)
                || !jwtService.isTokenValid(confirmToken, user, TokenType.CONFIRM_TOKEN)) {
            throw new AppException(ErrorCode.VERIFY_EMAIL_FAILED);
        }
        user.setStatus(StatusUser.ACTIVE);
        userRepository.save(user);
    }

    @Override
    public void updatePassword(Long id, UpdatePasswordRequest request) {
        User user = findUserById(id);
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.PASSWORD_MISMATCH);
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) throws MessagingException, UnsupportedEncodingException {
        User user = findUserByEmail(request.getEmail());
        if (!user.getStatus().equals(StatusUser.ACTIVE)) {
            throw new AppException(ErrorCode.ACCOUNT_NOT_ACTIVE);
        }
        mailService.sendResetLink(user);
    }

    @Override
    public void resetPassword(String token, ResetPasswordRequest request) {
        String email = jwtService.extractEmail(token, TokenType.RESET_TOKEN);
        User user = findUserByEmail(email);
        if (!jwtService.isTokenValid(token, user, TokenType.RESET_TOKEN)
                || !whitelistTokenService.existsByToken(token)) {
            throw new InvalidTokenException();
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        whitelistTokenService.deleteByToken(token);
        userRepository.save(user);
    }

    @Override
    public TokenResponse<UserResponse> createPassword(String token, UserCreationPasswordRequest request) {
        String email = jwtService.extractEmail(token, TokenType.CREATE_PASSWORD_TOKEN);
        User user = findUserByEmail(email);
        if (StringUtils.isBlank(user.getPassword()) && user.getNoPassword()
                && jwtService.isTokenValid(token, user, TokenType.CREATE_PASSWORD_TOKEN)) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setNoPassword(false);
            userRepository.save(user);
        } else {
            String message = messageSource.getMessage("user.password.exists", null, LocaleContextHolder.getLocale());
            throw new ResourceConflictException(message);
        }
        String accessToken = jwtService.generateAccessToken(user, AccountType.USER);
        String refreshToken = jwtService.generateRefreshToken(user, AccountType.USER);
        whitelistTokenService.createToken(accessToken, TokenType.ACCESS_TOKEN, user.getEmail());
        whitelistTokenService.createToken(refreshToken, TokenType.REFRESH_TOKEN, user.getEmail());
        if (user.getStatus().equals(StatusUser.PENDING)) {
            user.setStatus(StatusUser.ACTIVE);
        }
        UserResponse data = userMapper.toDTO(user);
        data.setRole(user.getRole().getRole());

        return TokenResponse.<UserResponse>builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .data(data)
                .createPasswordToken(null)
                .build();
    }

    @Override
    public UserResponse signUp(UserRequest request) throws MessagingException, UnsupportedEncodingException {
        log.info("Signing up user with email {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            String message = messageSource.getMessage("user.email.exists", new Object[] { request.getEmail() },
                    LocaleContextHolder.getLocale());
            throw new ResourceConflictException(message);
        }
        Role role = roleService.findRoleByRoleName(UserRole.JOB_SEEKER.getName());
        User user = User.builder()
                .role(role)
                .email(request.getEmail())
                .fullName(request.getFullName())
                .password(passwordEncoder.encode(request.getPassword()))
                .status(StatusUser.PENDING)
                .noPassword(false)
                .build();

        userRepository.save(user);
        mailService.sendConfirmLink(user);
        UserResponse response = userMapper.toDTO(user);
        response.setRole(user.getRole().getRole());
        log.info("User signed up successfully with id = {}", user.getId());
        return response;
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Deleting user with ID: {}", id);

        User user = findUserById(id);
        userRepository.delete(user);

        log.info("User deleted successfully with ID: {}", id);
    }

    @Override
    public User findUserByEmail(String email) {
        log.info("Looking up user by email {}", email);

        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User with email {} not found", email);
                    String message = messageSource.getMessage("user.not.found.by.email", new Object[] { email },
                            LocaleContextHolder.getLocale());
                    return new ResourceNotFoundException(message);
                });
    }

    private User findUserById(Long id) {
        log.debug("Looking up user by ID: {}", id);

        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User with ID {} not found", id);
                    String message = messageSource.getMessage("user.not.found", new Object[] { id },
                            LocaleContextHolder.getLocale());
                    return new ResourceNotFoundException(message);
                });
    }

}
