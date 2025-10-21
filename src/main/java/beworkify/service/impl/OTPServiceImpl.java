package beworkify.service.impl;

import beworkify.entity.OTPCode;
import beworkify.repository.OTPRepository;
import beworkify.service.OTPService;
import beworkify.util.AppUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OTPServiceImpl implements OTPService {
    private final OTPRepository repository;

    public void createOTPCode(String code, String email) {
        repository.save(OTPCode.builder().code(code).email(email).build());
    }

    public void deleteOTPCode(String code) {
        repository.deleteByCode(code);
    }

    public boolean isOTPCodeValid(String code, String email, int minutes) {
        OTPCode otpCode = repository.findByCode(code).orElse(null);
        if (otpCode == null || !otpCode.getEmail().equals(email)) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiredTime = otpCode.getCreatedAt().plusMinutes(minutes);

        return expiredTime.isAfter(now);
    }

    public String generateCode(String email){
        String code = AppUtils.generateOtp(8);
        createOTPCode(code, email);
        return code;
    }
}
