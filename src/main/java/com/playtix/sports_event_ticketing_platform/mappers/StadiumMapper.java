package com.playtix.sports_event_ticketing_platform.mappers;

import java.util.List;

import org.mapstruct.Mapper;

import com.playtix.sports_event_ticketing_platform.domain.dto.StadiumDto;
import com.playtix.sports_event_ticketing_platform.domain.entity.Stadium;

@Mapper(
    componentModel = "spring"
)
public interface StadiumMapper {

    StadiumDto toStadiumDto(Stadium stadium);

    List<StadiumDto> toStadiumDtoList(List<Stadium> stadiums);
    
}
