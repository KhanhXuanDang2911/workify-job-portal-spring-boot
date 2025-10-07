package beworkify.entity;

import beworkify.enumeration.TokenType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "whitelist_token", indexes = {
                @Index(name = "idx_whitelist_token_email", columnList = "email"),
                @Index(name = "idx_whitelist_token_expiredTime", columnList = "expiredTime")
})
public class WhitelistToken extends BaseEntity {
        @Column(nullable = false)
        private String email;
        @Column(unique = true, nullable = false, columnDefinition = "TEXT")
        private String token;
        @Enumerated(EnumType.STRING)
        private TokenType tokenType;
        private LocalDateTime expiredTime;
}
