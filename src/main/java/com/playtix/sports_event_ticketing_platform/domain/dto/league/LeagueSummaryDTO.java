package com.playtix.sports_event_ticketing_platform.domain.dto.league;

import java.util.UUID;

public record LeagueSummaryDTO(
        UUID id,
        String name,
        String country,
        String season,
        Integer numberOfTeams,
        String sportName,
        Integer matchesCount
) {}