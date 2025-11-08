
package beworkify.configuration;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
public class CacheConfig {

	@Bean
	public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory,
			GenericJackson2JsonRedisSerializer redisSerializer) {
		Map<String, RedisCacheConfiguration> cacheConfigs = new HashMap<>();
		cacheConfigs.put("roles", cacheConfiguration(redisSerializer).entryTtl(Duration.ofDays(7)));
		cacheConfigs.put("provinces", cacheConfiguration(redisSerializer).entryTtl(Duration.ofDays(2)));
		cacheConfigs.put("districts", cacheConfiguration(redisSerializer).entryTtl(Duration.ofDays(2)));
		cacheConfigs.put("categories_job", cacheConfiguration(redisSerializer).entryTtl(Duration.ofDays(2)));
		cacheConfigs.put("categories_job_count", cacheConfiguration(redisSerializer).entryTtl(Duration.ofSeconds(90)));
		cacheConfigs.put("categories_post", cacheConfiguration(redisSerializer).entryTtl(Duration.ofDays(2)));
		cacheConfigs.put("industries", cacheConfiguration(redisSerializer).entryTtl(Duration.ofDays(2)));
		cacheConfigs.put("posts", cacheConfiguration(redisSerializer).entryTtl(Duration.ofDays(2)));
		cacheConfigs.put("jobs", cacheConfiguration(redisSerializer).entryTtl(Duration.ofDays(2)));
		cacheConfigs.put("jobs_top_attractive", cacheConfiguration(redisSerializer).entryTtl(Duration.ofSeconds(30)));
		cacheConfigs.put("saved_jobs", cacheConfiguration(redisSerializer).entryTtl(Duration.ofMinutes(30)));
		cacheConfigs.put("employers", cacheConfiguration(redisSerializer).entryTtl(Duration.ofSeconds(30)));
		cacheConfigs.put("employers_top_hiring", cacheConfiguration(redisSerializer).entryTtl(Duration.ofSeconds(10)));
		cacheConfigs.put("users", cacheConfiguration(redisSerializer).entryTtl(Duration.ofMinutes(10)));

		return RedisCacheManager.builder(redisConnectionFactory).cacheDefaults(cacheConfiguration(redisSerializer))
				.withInitialCacheConfigurations(cacheConfigs).build();
	}

	@Bean
	public GenericJackson2JsonRedisSerializer redisSerializer() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(),
				ObjectMapper.DefaultTyping.EVERYTHING);
		return new GenericJackson2JsonRedisSerializer(objectMapper);
	}

	@Bean
	public RedisCacheConfiguration cacheConfiguration(GenericJackson2JsonRedisSerializer serializer) {
		return RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(10)).disableCachingNullValues()
				.computePrefixWith(cacheName -> cacheName + ":")
				.serializeKeysWith(
						RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
				.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));
	}
}
