package com.playtix.sports_event_ticketing_platform.domain.dto.match;

import java.time.LocalDateTime;
import java.util.UUID;

public record MatchSummaryDto(
    UUID id,
    String homeTeam,
    String awayTeam,
    LocalDateTime matchDate,
    String stadiumName,
    String tournamentName,
    String leagueName
) {}
