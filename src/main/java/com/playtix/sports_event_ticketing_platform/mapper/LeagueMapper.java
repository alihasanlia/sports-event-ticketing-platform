package com.playtix.sports_event_ticketing_platform.mapper;

import com.playtix.sports_event_ticketing_platform.domain.dto.league.LeagueResponseDTO;
import com.playtix.sports_event_ticketing_platform.domain.dto.league.LeagueSummaryDTO;
import com.playtix.sports_event_ticketing_platform.domain.entity.League;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", uses = SportMapper.class)
public interface LeagueMapper {
    
    LeagueMapper INSTANCE = Mappers.getMapper(LeagueMapper.class);
    
    @Mapping(target = "sportDto", source = "sport")
    @Mapping(target = "matchesCount", expression = "java(league.getMatchesCount())")
    @Mapping(target = "fullName", expression = "java(league.getFullName())")
    LeagueResponseDTO toResponseDTO(League league);
    
    @Mapping(target = "sportName", source = "sport.name")
    @Mapping(target = "matchesCount", expression = "java(league.getMatchesCount())")
    LeagueSummaryDTO toSummaryDTO(League league);
    
    List<LeagueResponseDTO> toResponseDTOList(List<League> leagues);
    List<LeagueSummaryDTO> toSummaryDTOList(List<League> leagues);
    
}