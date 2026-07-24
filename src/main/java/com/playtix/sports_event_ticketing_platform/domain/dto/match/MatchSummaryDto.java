package com.playtix.sports_event_ticketing_platform.domain.dto.match;

import java.time.LocalDateTime;

public record MatchSummaryDto(
    String homeTeam,
    String awayTeam,
    LocalDateTime matchDate,
    String stadiumName
) {}
