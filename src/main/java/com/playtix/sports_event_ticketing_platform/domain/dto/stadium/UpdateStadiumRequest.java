package com.playtix.sports_event_ticketing_platform.domain.dto.stadium;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.util.UUID;

public record UpdateStadiumRequest(
    UUID id,
    
    @NotBlank(message = "Stadium name is required")
    String name,
    
    @Positive(message = "Capacity must be positive")
    int capacity,
    
    String address
) {}