
package beworkify.entity.redis;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RedisHash("otp_code")
public class RedisOTPCode {
	@Id
	private String code;

	private String email;

	@TimeToLive
	private Long expiredTime;
}
