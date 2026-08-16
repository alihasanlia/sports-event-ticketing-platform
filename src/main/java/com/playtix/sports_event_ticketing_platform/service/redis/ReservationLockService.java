package com.playtix.sports_event_ticketing_platform.service.redis;

import com.playtix.sports_event_ticketing_platform.config.RedisCacheProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class ReservationLockService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisCacheProperties redisCacheProperties;

    /**
     * Attempts to acquire a temporary reservation lock for a specific ticket.
     * Uses Redis SETNX (setIfAbsent) with TTL to prevent concurrent double-booking.
     *
     * @param ticketId UUID or Long representation of ticket
     * @param userId   User ID holding the temporary lock
     * @return true if lock acquired, false if already locked
     */
    public boolean acquireLock(Object ticketId, Object userId) {
        String lockKey = "reservation-lock:ticket:" + ticketId;
        Boolean success = redisTemplate.opsForValue().setIfAbsent(
                lockKey,
                userId.toString(),
                Duration.ofSeconds(redisCacheProperties.getReservationLockTtl())
        );
        return Boolean.TRUE.equals(success);
    }

    /**
     * Release the temporary lock if held by the given userId.
     */
    public boolean releaseLock(Object ticketId, Object userId) {
        String lockKey = "reservation-lock:ticket:" + ticketId;
        Object currentOwner = redisTemplate.opsForValue().get(lockKey);
        if (currentOwner != null && currentOwner.toString().equals(userId.toString())) {
            redisTemplate.delete(lockKey);
            return true;
        }
        return false;
    }

    /**
     * Check if a ticket currently has an active temporary reservation lock in Redis.
     */
    public boolean isLocked(Object ticketId) {
        String lockKey = "reservation-lock:ticket:" + ticketId;
        return Boolean.TRUE.equals(redisTemplate.hasKey(lockKey));
    }
}
