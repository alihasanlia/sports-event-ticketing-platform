package com.playtix.sports_event_ticketing_platform.domain.dto.match;

import java.time.LocalDateTime;
import java.util.UUID;

public record MatchDetailsDto(
    UUID id,
    String homeTeam,
    UUID homeTeamId,
    String awayTeam,
    UUID awayTeamId,
    LocalDateTime matchDate,
    String stadiumName,
    String sport,
    UUID sportId,
    String tournamentName,
    String leagueName
) {}
