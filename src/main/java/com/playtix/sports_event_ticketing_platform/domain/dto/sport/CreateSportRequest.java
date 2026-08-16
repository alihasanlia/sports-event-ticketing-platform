package com.playtix.sports_event_ticketing_platform.domain.dto.sport;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import com.playtix.sports_event_ticketing_platform.domain.entity.SportType;

public record CreateSportRequest(
    @NotNull(message = "Sport type is required")
    SportType name,
    
    String description,
    
    @PositiveOrZero(message = "Number of players cannot be negative")
    int numberOfPlayers
) {}