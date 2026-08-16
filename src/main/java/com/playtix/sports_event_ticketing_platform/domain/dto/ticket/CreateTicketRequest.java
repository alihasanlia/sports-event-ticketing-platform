package com.playtix.sports_event_ticketing_platform.domain.dto.ticket;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateTicketRequest(
    @NotBlank(message = "Seat number is required")
    String seatNumber,
    
    String rowNumber,
    
    String sectionNumber,
    
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    BigDecimal price,
    
    @NotNull(message = "Ticket category ID is required")
    UUID ticketCategoryId,
    
    @NotNull(message = "Match ID is required")
    UUID matchId
) {}