package beworkify.service;

public interface OTPService {
    void createOTPCode(String code, String email);

    void deleteOTPCode(String code);

    boolean isOTPCodeValid(String code, String email, int minutes);

    String generateCode(String email);
}
