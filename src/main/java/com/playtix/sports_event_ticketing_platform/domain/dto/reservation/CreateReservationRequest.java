package com.playtix.sports_event_ticketing_platform.domain.dto.reservation;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record CreateReservationRequest(
    @NotNull(message = "User ID is required")
    UUID userId,
    
    @NotNull(message = "Ticket ID is required")
    UUID ticketId,
    
    @Positive(message = "Quantity must be positive")
    int quantity
) {}