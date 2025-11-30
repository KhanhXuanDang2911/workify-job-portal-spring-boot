package beworkify.util;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

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
