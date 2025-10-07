package beworkify.service;

import beworkify.dto.request.EmployerRequest;
import beworkify.dto.request.ForgotPasswordRequest;
import beworkify.dto.request.ResetPasswordRequest;
import beworkify.dto.request.UpdatePasswordRequest;
import beworkify.dto.response.EmployerResponse;
import beworkify.dto.response.PageResponse;
import beworkify.entity.Employer;
import beworkify.enumeration.LevelCompanySize;
import jakarta.mail.MessagingException;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.util.List;

public interface EmployerService {

    PageResponse<List<EmployerResponse>> getEmployersWithPaginationAndKeywordAndSorts(int pageNumber, int pageSize,
            List<String> sorts, String keyword, LevelCompanySize companySize, Long provinceId, boolean isAdmin);

    EmployerResponse signUpEmployer(EmployerRequest request) throws MessagingException, UnsupportedEncodingException;

    void verifyEmailEmployer(String confirmToken);

    EmployerResponse getEmployerById(Long id);

    EmployerResponse updateEmployer(Long id, EmployerRequest request, MultipartFile avatar, MultipartFile background);

    EmployerResponse createEmployer(EmployerRequest request, MultipartFile avatar, MultipartFile background);

    void deleteEmployer(Long id);

    EmployerResponse updateProfileEmployer(Long id, EmployerRequest request);

    EmployerResponse uploadAvatar(Long id, MultipartFile avatar);

    EmployerResponse uploadBackground(Long id, MultipartFile background);

    Employer findEmployerByEmail(String email);

    void forgotPassword(ForgotPasswordRequest request) throws MessagingException, UnsupportedEncodingException;

    void resetPassword(String token, ResetPasswordRequest request);

    void updatePassword(Long employerId, UpdatePasswordRequest request);
}
