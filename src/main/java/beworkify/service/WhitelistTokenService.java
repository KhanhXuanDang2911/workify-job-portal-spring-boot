package beworkify.service;

import beworkify.enumeration.TokenType;

public interface WhitelistTokenService {
    void createToken(String token, TokenType tokenType, String email);

    void deleteByToken(String token);

    boolean existsByToken(String token);
}
