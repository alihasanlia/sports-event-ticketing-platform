package com.playtix.sports_event_ticketing_platform.domain.dto.tournament;

import com.playtix.sports_event_ticketing_platform.domain.entity.tournament.TournamentStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public record TournamentSummaryDTO(
        UUID id,
        String name,
        TournamentStatus status,
        LocalDateTime startDate,
        LocalDateTime endDate,
        String sportName,
        Integer matchesCount
) {}