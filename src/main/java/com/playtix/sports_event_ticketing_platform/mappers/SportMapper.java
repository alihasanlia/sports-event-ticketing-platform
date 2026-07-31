package com.playtix.sports_event_ticketing_platform.mappers;

import java.util.List;

import org.mapstruct.Mapper;

import com.playtix.sports_event_ticketing_platform.domain.dto.SportDto;
import com.playtix.sports_event_ticketing_platform.domain.entity.Sport;

@Mapper(componentModel = "spring")
public interface SportMapper {

    SportDto toSprotDto(Sport sport);

    List<SportDto> toSportDtoList(List<Sport> sports);
    
}
