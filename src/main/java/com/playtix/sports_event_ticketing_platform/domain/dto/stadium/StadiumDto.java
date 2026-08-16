package com.playtix.sports_event_ticketing_platform.domain.dto.stadium;

import java.util.UUID;

public record StadiumDto(
    UUID id,
    String name,
    String city,
    int capacity,
    String address
) {}
