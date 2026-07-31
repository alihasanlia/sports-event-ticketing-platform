package com.playtix.sports_event_ticketing_platform.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.playtix.sports_event_ticketing_platform.domain.dto.match.MatchDetailsDto;
import com.playtix.sports_event_ticketing_platform.domain.dto.match.MatchSummaryDto;
import com.playtix.sports_event_ticketing_platform.domain.entity.match.Match;

@Mapper(
    componentModel = "spring",
    uses = {
        TeamMapper.class,
        SportMapper.class,
        StadiumMapper.class
    }
)
public interface MatchMapper {

    @Mapping(target = "homeTeam", expression = "java(match.getHomeTeam().getName())")
    @Mapping(target = "awayTeam", expression = "java(match.getAwayTeam().getName())")
    @Mapping(target = "stadiumName", expression = "java(match.getStadium().getName())")
    @Mapping(target = "tournamentName", expression = "java(match.getTournament().getName())")
    @Mapping(target = "leagueName", expression = "java(match.getLeague().getName())")
    MatchSummaryDto toSummaryDto(Match match);

    List<MatchSummaryDto> toSummaryDtoList(List<Match> matches);

    @Mapping(target = "homeTeamDto", source = "homeTeam")
    @Mapping(target = "awayTeamDto", source = "awayTeam")
    @Mapping(target = "stadiumDto", source = "stadium")
    @Mapping(target = "tournamentName", expression = "java(match.getTournament().getName())")
    @Mapping(target = "leagueName", expression = "java(match.getLeague().getName())")
    MatchDetailsDto toDetailsDto(Match match);

    List<MatchDetailsDto> toDetailsDtoList(List<Match> matches);
    
}
