
package beworkify.service.redis;

import beworkify.enumeration.TokenType;

public interface RedisTokenService {
	void save(String accessToken, TokenType tokenType, Long expiredTime);

	void saveAccessToken(String accessToken);

	void saveRefreshToken(String refreshToken);

	void saveResetToken(String resetToken);

	boolean existsByJwtId(String token, TokenType tokenType);

	void deleteByJwtId(String token, TokenType tokenType);
}
