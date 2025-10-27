package beworkify.service.impl;

import beworkify.enumeration.AccountType;
import beworkify.enumeration.TokenType;
import beworkify.exception.InvalidTokenException;
import beworkify.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.secret-key}")
    private String SECRET_KEY;

    @Value("${jwt.refresh-key}")
    private String REFRESH_KEY;

    @Value("${jwt.reset-key}")
    private String RESET_KEY;

    @Value("${jwt.confirm-key}")
    private String CONFIRM_KEY;

    @Value("${jwt.create-password-key}")
    private String CREATE_PASSWORD_KEY;

    @Value("${jwt.expiry-hour}")
    private long expiryHour;

    @Value("${jwt.expiry-day}")
    private long expiryDay;

    @Value("${jwt.expiry-minute}")
    private long expiryMinute;

    @Value("${spring.application.name}")
    private String provider;

    @Override
    public String generateAccessToken(UserDetails userDetails, AccountType accountType) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * expiryHour))
                .setIssuer(provider)
                .claim("ACCOUNT_TYPE", accountType.getType())
                .signWith(getKey(TokenType.ACCESS_TOKEN), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String generateRefreshToken(UserDetails userDetails, AccountType accountType) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setId(UUID.randomUUID().toString())
                .setIssuer(provider)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * expiryDay))
                .claim("ACCOUNT_TYPE", accountType.getType())
                .signWith(getKey(TokenType.REFRESH_TOKEN), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String generateToken(UserDetails userDetails, TokenType tokenType, long hour) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuer(provider)
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * hour))
                .signWith(getKey(tokenType))
                .compact();
    }

    @Override
    public Claims extractAllClaims(String token, TokenType tokenType) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getKey(tokenType))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new InvalidTokenException();
        }
    }

    @Override
    public String extractEmail(String token, TokenType tokenType) {
        return extractAllClaims(token, tokenType).getSubject();
    }

    @Override
    public String extractJwtId(String token, TokenType tokenType) {
        return extractAllClaims(token, tokenType).getId();
    }

    @Override
    public String extractAccountType(String token, TokenType tokenType) {
        return extractAllClaims(token, tokenType).get("ACCOUNT_TYPE", String.class);
    }

    @Override
    public Date extractExpiration(String token, TokenType tokenType) {
        return extractAllClaims(token, tokenType).getExpiration();
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails, TokenType tokenType) {
        final String email = extractEmail(token, tokenType);
        if (!tokenType.equals(TokenType.CONFIRM_TOKEN))
            return userDetails.isEnabled() && email.equals(userDetails.getUsername())
                    && !isTokenExpired(extractExpiration(token, tokenType));
        else
            return email.equals(userDetails.getUsername()) && !isTokenExpired(extractExpiration(token, tokenType));

    }

    private boolean isTokenExpired(Date expiration) {
        return expiration.before(new Date(System.currentTimeMillis()));
    }

    private Key getKey(TokenType tokenType) {
        byte[] keyBytes;
        keyBytes = switch (tokenType) {
            case ACCESS_TOKEN -> Decoders.BASE64.decode(SECRET_KEY);
            case REFRESH_TOKEN -> Decoders.BASE64.decode(REFRESH_KEY);
            case RESET_TOKEN -> Decoders.BASE64.decode(RESET_KEY);
            case CONFIRM_TOKEN -> Decoders.BASE64.decode(CONFIRM_KEY);
            case CREATE_PASSWORD_TOKEN -> Decoders.BASE64.decode(CREATE_PASSWORD_KEY);
        };
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
