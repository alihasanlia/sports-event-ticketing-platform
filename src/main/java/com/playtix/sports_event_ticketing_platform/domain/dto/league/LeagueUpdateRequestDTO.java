package com.playtix.sports_event_ticketing_platform.domain.dto.league;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record LeagueUpdateRequestDTO(
        UUID id,
        
        @NotBlank(message = "League name is required")
        String name,
        
        String country,
        String season,
        Integer numberOfTeams,
        String description,
        UUID sportId
) {}