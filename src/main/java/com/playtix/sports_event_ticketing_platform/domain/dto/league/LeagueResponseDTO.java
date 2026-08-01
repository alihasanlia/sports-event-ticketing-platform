package com.playtix.sports_event_ticketing_platform.domain.dto.league;

import java.util.UUID;

import com.playtix.sports_event_ticketing_platform.domain.dto.sport.SportDto;


public record LeagueResponseDTO(
    UUID id,
    String name,
    String country,
    String season,
    Integer numberOfTeams,
    String description,
    SportDto sportDto,
    Integer matchesCount,
    String fullName
) {}