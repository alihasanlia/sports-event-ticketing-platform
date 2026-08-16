package com.playtix.sports_event_ticketing_platform.domain.dto.stadium;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record CreateStadiumRequest(
    @NotBlank(message = "Stadium name is required")
    String name,
    
    @NotBlank(message = "City is required")
    String city,
    
    @Positive(message = "Capacity must be positive")
    int capacity,
    
    String address
) {}