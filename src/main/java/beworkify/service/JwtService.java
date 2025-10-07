package beworkify.service;

import beworkify.enumeration.AccountType;
import beworkify.enumeration.TokenType;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

public interface JwtService {

    String generateAccessToken(UserDetails userDetails, AccountType accountType);

    String generateRefreshToken(UserDetails userDetails, AccountType accountType);

    String generateToken(UserDetails userDetails, TokenType tokenType, long hour);

    Claims extractAllClaims(String token, TokenType tokenType);

    String extractEmail(String token, TokenType tokenType);

    String extractAccountType(String token, TokenType tokenType);

    Date extractExpiration(String token, TokenType tokenType);

    boolean isTokenValid(String token, UserDetails userDetails, TokenType tokenType);
}
