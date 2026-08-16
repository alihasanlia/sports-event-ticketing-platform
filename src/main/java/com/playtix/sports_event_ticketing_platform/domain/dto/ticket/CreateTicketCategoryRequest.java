package com.playtix.sports_event_ticketing_platform.domain.dto.ticket;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import com.playtix.sports_event_ticketing_platform.domain.entity.ticket.Category;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateTicketCategoryRequest(
    @NotNull(message = "Category is required")
    Category category,
    
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    BigDecimal price,
    
    @NotNull(message = "Total capacity is required")
    @Positive(message = "Total capacity must be positive")
    int totalCapacity,
    
    @NotNull(message = "Match ID is required")
    UUID matchId,
    
    String description
) {}