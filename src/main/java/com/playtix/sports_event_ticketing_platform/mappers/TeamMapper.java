package com.playtix.sports_event_ticketing_platform.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.playtix.sports_event_ticketing_platform.domain.dto.TeamDto;
import com.playtix.sports_event_ticketing_platform.domain.entity.Team;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface TeamMapper {

    TeamDto toTeamDto(Team team);

    List<TeamDto> toTeamDtoList(List<Team> teams);
    
}
