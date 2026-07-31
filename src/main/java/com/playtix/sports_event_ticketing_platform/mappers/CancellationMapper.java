package com.playtix.sports_event_ticketing_platform.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.playtix.sports_event_ticketing_platform.domain.dto.CancellationDto;
import com.playtix.sports_event_ticketing_platform.domain.entity.TicketCancellation;


@Mapper(componentModel = "spring", uses = TicketMapper.class)
public interface CancellationMapper {

    @Mapping(target = "ticketSummaryDto", source = "ticket")
    CancellationDto toCancellationDto(TicketCancellation ticketCancellation);

    List<CancellationDto> toCancellationDtoList(List<TicketCancellation> ticketCancellations);
    
}
