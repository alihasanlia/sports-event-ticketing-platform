package com.playtix.sports_event_ticketing_platform.mapper;

import com.playtix.sports_event_ticketing_platform.domain.dto.tournament.TournamentResponseDTO;
import com.playtix.sports_event_ticketing_platform.domain.dto.tournament.TournamentSummaryDTO;
import com.playtix.sports_event_ticketing_platform.domain.entity.tournament.Tournament;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", uses = SportMapper.class)
public interface TournamentMapper {
    
    TournamentMapper INSTANCE = Mappers.getMapper(TournamentMapper.class);
    
    @Mapping(target = "sportDto", source = "sport")
    @Mapping(target = "matchesCount", expression = "java(tournament.getMatchesCount())")
    @Mapping(target = "isActive", expression = "java(tournament.isActive())")
    @Mapping(target = "isFinished", expression = "java(tournament.isFinished())")
    @Mapping(target = "isUpcoming", expression = "java(tournament.isUpcoming())")
    @Mapping(target = "isCancelled", expression = "java(tournament.isCancelled())")
    @Mapping(target = "canAddMatch", expression = "java(tournament.canAddMatch())")
    TournamentResponseDTO toResponseDTO(Tournament tournament);
    
    @Mapping(target = "sportName", source = "sport.name")
    @Mapping(target = "matchesCount", expression = "java(tournament.getMatchesCount())")
    TournamentSummaryDTO toSummaryDTO(Tournament tournament);
    
    List<TournamentResponseDTO> toResponseDTOList(List<Tournament> tournaments);
    List<TournamentSummaryDTO> toSummaryDTOList(List<Tournament> tournaments);
    
}