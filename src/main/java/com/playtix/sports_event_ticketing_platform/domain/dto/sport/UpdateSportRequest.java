package com.playtix.sports_event_ticketing_platform.domain.dto.sport;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.UUID;

public record UpdateSportRequest(
    UUID id,
    
    String description,
    
    @PositiveOrZero(message = "Number of players cannot be negative")
    int numberOfPlayers
) {}