package beworkify.util;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Utility class for interacting with Redis. Provides methods for cache eviction and other Redis
 * operations.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
public class RedisUtils {
  private final RedisTemplate<String, Object> redisTemplate;

  public void evictCacheByPattern(String pattern) {
    Set<String> keys = redisTemplate.keys(pattern);
    if (!keys.isEmpty()) {
      redisTemplate.delete(keys);
    }
  }
}
