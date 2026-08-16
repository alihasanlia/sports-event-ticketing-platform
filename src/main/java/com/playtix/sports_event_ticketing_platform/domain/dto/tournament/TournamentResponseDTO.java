package com.playtix.sports_event_ticketing_platform.domain.dto.tournament;

import com.playtix.sports_event_ticketing_platform.domain.dto.sport.SportDto;
import com.playtix.sports_event_ticketing_platform.domain.entity.tournament.TournamentStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public record TournamentResponseDTO(
        UUID id,
        String name,
        String description,
        LocalDateTime startDate,
        LocalDateTime endDate,
        TournamentStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        SportDto sportDto,
        Integer matchesCount,
        Boolean isActive,
        Boolean isFinished,
        Boolean isUpcoming,
        Boolean isCancelled,
        Boolean canAddMatch
) {}