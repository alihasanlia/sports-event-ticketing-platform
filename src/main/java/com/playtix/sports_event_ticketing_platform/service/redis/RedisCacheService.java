package com.playtix.sports_event_ticketing_platform.service.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RedisCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    public void put(String key, Object value, Duration ttl) {
        redisTemplate.opsForValue().set(key, value, ttl);
    }

    public Optional<Object> get(String key) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(key));
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

    public boolean exists(String key) {
        Boolean exists = redisTemplate.hasKey(key);
        return Boolean.TRUE.equals(exists);
    }

    public void deleteByPattern(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

}
