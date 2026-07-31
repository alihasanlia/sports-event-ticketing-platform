package com.playtix.sports_event_ticketing_platform.domain.dto.match;

import java.time.LocalDateTime;
import java.util.UUID;

import com.playtix.sports_event_ticketing_platform.domain.dto.SportDto;
import com.playtix.sports_event_ticketing_platform.domain.dto.StadiumDto;
import com.playtix.sports_event_ticketing_platform.domain.dto.TeamDto;
import com.playtix.sports_event_ticketing_platform.domain.entity.SportType;

public record MatchDetailsDto(
    UUID id,
    TeamDto homeTeamDto,
    TeamDto awayTeamDto,
    LocalDateTime matchDate,
    StadiumDto stadiumDto,
    SportType sportType,
    String tournamentName,
    String leagueName
) {}
