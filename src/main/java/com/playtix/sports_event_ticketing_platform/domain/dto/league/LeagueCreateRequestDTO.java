package com.playtix.sports_event_ticketing_platform.domain.dto.league;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record LeagueCreateRequestDTO (
    @NotBlank(message = "League name is required")
    String name,
    String country,
    String season,
    Integer numberOfTeams,
    String description,
    @NotNull(message = "Sport ID is required")
    UUID sportId
) {}