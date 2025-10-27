package beworkify.service.redis;


public interface RedisOTPCodeService {
    String generateAndSaveOTPCode(String email, long minutes);

    boolean isValidCode(String code, String email);

     void deleteOTPCode(String code);
}
