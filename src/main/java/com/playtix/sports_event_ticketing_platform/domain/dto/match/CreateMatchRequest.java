package com.playtix.sports_event_ticketing_platform.domain.dto.match;

import jakarta.validation.constraints.NotNull;
import com.playtix.sports_event_ticketing_platform.domain.entity.SportType;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateMatchRequest(
    @NotNull(message = "Match date is required")
    LocalDateTime matchDate,
    
    @NotNull(message = "Sport type is required")
    SportType sportType,
    
    @NotNull(message = "Home team ID is required")
    UUID homeTeamId,
    
    @NotNull(message = "Away team ID is required")
    UUID awayTeamId,
    
    @NotNull(message = "Stadium ID is required")
    UUID stadiumId,
    
    UUID leagueId,
    
    UUID tournamentId
) {}