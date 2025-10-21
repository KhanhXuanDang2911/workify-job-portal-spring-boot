package beworkify.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "otp_code", indexes = {
        @Index(name="ix_otp_code_code", columnList = "code")
})
public class OTPCode extends BaseEntity {
    @Column(nullable = false, length = 8)
    private String code;
    @Column(nullable = false, length = 1000)
    private String email;
}
