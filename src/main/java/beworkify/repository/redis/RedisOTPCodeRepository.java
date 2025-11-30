package beworkify.repository.redis;

import beworkify.entity.redis.RedisOTPCode;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisOTPCodeRepository extends CrudRepository<RedisOTPCode, String> {}
