package beworkify.service.redis.impl;

import beworkify.entity.redis.RedisToken;
import beworkify.enumeration.TokenType;
import beworkify.repository.redis.RedisTokenRepository;
import beworkify.service.JwtService;
import beworkify.service.redis.RedisTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisTokenServiceImpl implements RedisTokenService {
    private final RedisTokenRepository redisTokenRepository;
    private final JwtService jwtService;

    @Value("${jwt.expiry-hour}")
    private long expiryHour;

    @Value("${jwt.expiry-day}")
    private long expiryDay;

    @Override
    public void save(String accessToken, TokenType tokenType, Long expiredTime) {
        redisTokenRepository.save(RedisToken.builder()
                .jwtId(jwtService.extractJwtId(accessToken, tokenType))
                .expiredTime(expiredTime)
                .build());
    }

    @Override
    public void saveAccessToken(String accessToken) {
        save(accessToken, TokenType.ACCESS_TOKEN,  60 * 60 * expiryHour);
    }

    @Override
    public void saveRefreshToken(String refreshToken) {
        save(refreshToken, TokenType.REFRESH_TOKEN,  60 * 60 * 24 * expiryDay);
    }

    @Override
    public void saveResetToken(String resetToken) {
        save(resetToken, TokenType.RESET_TOKEN, (long) (60 * 60));
    }

    @Override
    public boolean existsByJwtId(String token, TokenType tokenType) {
        return redisTokenRepository.existsById(jwtService.extractJwtId(token, tokenType));
    }

    @Override
    public void deleteByJwtId(String token, TokenType tokenType) {
        redisTokenRepository.deleteById(jwtService.extractJwtId(token, tokenType));
    }

}
