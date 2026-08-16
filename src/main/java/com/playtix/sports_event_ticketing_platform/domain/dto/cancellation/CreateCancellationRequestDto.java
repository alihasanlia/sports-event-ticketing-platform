package com.playtix.sports_event_ticketing_platform.domain.dto.cancellation;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record CreateCancellationRequestDto(
    @NotNull(message = "Ticket ID is required")
    UUID ticketId,
    
    @NotNull(message = "User ID is required")
    UUID userId,
    
    @Size(max = 500, message = "Reason cannot exceed 500 characters")
    String reason
) {}
