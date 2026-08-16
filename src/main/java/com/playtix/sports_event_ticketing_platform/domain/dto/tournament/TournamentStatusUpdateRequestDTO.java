package com.playtix.sports_event_ticketing_platform.domain.dto.tournament;

import com.playtix.sports_event_ticketing_platform.domain.entity.tournament.TournamentStatus;
import jakarta.validation.constraints.NotNull;

public record TournamentStatusUpdateRequestDTO(
        @NotNull(message = "Status is required")
        TournamentStatus status
) {}