package com.playtix.sports_event_ticketing_platform.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.playtix.sports_event_ticketing_platform.domain.dto.team.TeamDto;
import com.playtix.sports_event_ticketing_platform.domain.entity.Team;

@Mapper(componentModel = "spring")
public interface TeamMapper {

    TeamDto toTeamDto(Team team);

    List<TeamDto> toTeamDtoList(List<Team> teams);
    
}
