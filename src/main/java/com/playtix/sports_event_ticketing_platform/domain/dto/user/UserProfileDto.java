package com.playtix.sports_event_ticketing_platform.domain.dto.user;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserProfileDto(
    UUID id,
    String firstname,
    String lastname,
    String email,
    String phoneNumber,
    String city,
    String profileImageUrl,
    LocalDateTime registrationDate,
    BigDecimal balance
) {}
