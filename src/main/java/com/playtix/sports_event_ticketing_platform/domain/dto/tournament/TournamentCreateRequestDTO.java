package com.playtix.sports_event_ticketing_platform.domain.dto.tournament;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

public record TournamentCreateRequestDTO(
        @NotBlank(message = "Tournament name is required")
        String name,
        
        String description,
        
        @Future(message = "Start date must be in the future")
        LocalDateTime startDate,
        
        LocalDateTime endDate,
        
        @NotNull(message = "Sport ID is required")
        UUID sportId
) {}