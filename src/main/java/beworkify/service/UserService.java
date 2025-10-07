package beworkify.service;

import beworkify.dto.request.*;
import beworkify.dto.response.PageResponse;
import beworkify.dto.response.TokenResponse;
import beworkify.dto.response.UserResponse;
import beworkify.entity.User;
import jakarta.mail.MessagingException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.util.List;

public interface UserService {

    UserResponse getUserById(Long id);

    UserResponse signUp(UserRequest request, boolean isMobile) throws MessagingException, UnsupportedEncodingException;

    void deleteUser(Long id);

    User findUserByEmail(String email);

    @Transactional(readOnly = true)
    PageResponse<List<UserResponse>> getUsersWithPaginationAndKeywordAndSorts(int pageNumber, int pageSize,
            List<String> sorts, String keyword);

    UserResponse createUser(UserRequest request, MultipartFile avatar);

    UserResponse updateUser(UserRequest request, MultipartFile avatar, Long id);

    UserResponse updateProfile(Long userId, UserRequest request);

    UserResponse updateAvatar(Long userId, MultipartFile avatar);

    void verifyEmailUser(String confirmToken);

    void updatePassword(Long userId, UpdatePasswordRequest request);

    void forgotPassword(ForgotPasswordRequest request, boolean isMobile) throws MessagingException, UnsupportedEncodingException;

    void resetPassword(String token, ResetPasswordRequest request);

    TokenResponse<UserResponse> createPassword(String token, UserCreationPasswordRequest request);
}
