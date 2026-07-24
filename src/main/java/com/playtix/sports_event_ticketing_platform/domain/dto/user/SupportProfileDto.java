package com.playtix.sports_event_ticketing_platform.domain.dto.user;

import java.time.LocalDateTime;
import java.util.UUID;

public record SupportProfileDto(
    UUID id,
    String firstName,
    String lastName,
    String email,
    String phoneNumber,
    String city,
    String profileImageUrl,
    LocalDateTime createdAt
) {}
