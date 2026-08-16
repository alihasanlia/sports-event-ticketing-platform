package com.playtix.sports_event_ticketing_platform.domain.dto.tournament;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.UUID;

public record TournamentUpdateRequestDTO(
        UUID id,
        
        @NotBlank(message = "Tournament name is required")
        String name,
        
        String description,
        
        @Future(message = "Start date must be in the future")
        LocalDateTime startDate,
        
        LocalDateTime endDate,
        
        UUID sportId
) {}