package com.playtix.sports_event_ticketing_platform.domain.dto.sport;

import java.util.UUID;

import com.playtix.sports_event_ticketing_platform.domain.entity.SportType;

public record SportDto(
    UUID id,
    SportType name,
    String description,
    int numberOfPlayers
) {}
