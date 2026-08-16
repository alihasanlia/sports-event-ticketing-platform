package com.playtix.sports_event_ticketing_platform.service.redis;

import com.playtix.sports_event_ticketing_platform.config.RedisCacheProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final RedisCacheService redisCacheService;
    private final RedisCacheProperties redisCacheProperties;
    private final Random random = new Random();

    /**
     * Generate a 6-digit OTP and store it in Redis for the given identifier (phone or email).
     * @param identifier phone number or email
     * @return the generated OTP
     */
    public String generateAndStoreOtp(String identifier) {
        String otp = String.format("%06d", random.nextInt(1000000));
        String key = getOtpKey(identifier);
        redisCacheService.put(key, otp, java.time.Duration.ofSeconds(redisCacheProperties.getOtpTtl()));
        return otp;
    }

    /**
     * Validate the OTP for the given identifier.
     * @param identifier phone number or email
     * @param otp the OTP to validate
     * @return true if OTP is valid and not expired, false otherwise
     */
    public boolean validateOtp(String identifier, String otp) {
        String key = getOtpKey(identifier);
        return redisCacheService.get(key)
                .map(storedOtp -> storedOtp.equals(otp))
                .orElse(false);
    }

    /**
     * Remove the OTP for the given identifier (e.g., after successful validation).
     * @param identifier phone number or email
     */
    public void removeOtp(String identifier) {
        String key = getOtpKey(identifier);
        redisCacheService.delete(key);
    }

    private String getOtpKey(String identifier) {
        if (identifier == null || identifier.isBlank()) {
            throw new IllegalArgumentException("Identifier cannot be null or blank");
        }
        // Simple heuristic: if contains '@', treat as email, else as phone
        if (identifier.contains("@")) {
            return RedisKeys.otpByEmail(identifier);
        } else {
            return RedisKeys.otpByPhone(identifier);
        }
    }
}
