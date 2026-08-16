package com.playtix.sports_event_ticketing_platform.domain.dto.ticket;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.UUID;

public record UpdateTicketCategoryRequest(
    @NotNull(message = "Category ID is required")
    UUID categoryId,
    
    @PositiveOrZero(message = "Remaining capacity cannot be negative")
    int remainingCapacity,
    
    String description
) {}