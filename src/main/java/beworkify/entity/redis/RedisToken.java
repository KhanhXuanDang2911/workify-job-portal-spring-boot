
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
@RedisHash("token")
public class RedisToken {
	@Id
	private String jwtId;

	@TimeToLive
	private Long expiredTime;
}
