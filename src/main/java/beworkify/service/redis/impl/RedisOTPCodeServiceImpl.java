
package beworkify.service.redis.impl;

import beworkify.entity.redis.RedisOTPCode;
import beworkify.repository.redis.RedisOTPCodeRepository;
import beworkify.service.redis.RedisOTPCodeService;
import beworkify.util.AppUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisOTPCodeServiceImpl implements RedisOTPCodeService {

	private final RedisOTPCodeRepository redisOTPCodeRepository;

	@Override
	public String generateAndSaveOTPCode(String email, long minutes) {
		String code = AppUtils.generateOtp(8);
		redisOTPCodeRepository.save(RedisOTPCode.builder().code(code).email(email).expiredTime(minutes * 60).build());
		return code;
	}

	@Override
	public boolean isValidCode(String code, String email) {
		RedisOTPCode otpCode = redisOTPCodeRepository.findById(code).orElse(null);
		return otpCode != null && otpCode.getEmail().equals(email);
	}

	@Override
	public void deleteOTPCode(String code) {
		redisOTPCodeRepository.deleteById(code);
	}
}
