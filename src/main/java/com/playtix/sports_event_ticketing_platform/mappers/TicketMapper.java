package com.playtix.sports_event_ticketing_platform.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.playtix.sports_event_ticketing_platform.domain.dto.ticket.TicketDetailsDto;
import com.playtix.sports_event_ticketing_platform.domain.dto.ticket.TicketSummaryDto;
import com.playtix.sports_event_ticketing_platform.domain.entity.ticket.Ticket;

@Mapper(
    componentModel = "spring",
    uses = {
        MatchMapper.class,
        TicketCategoryMapper.class
    }
)
public interface TicketMapper {

    TicketSummaryDto toSummaryDto(Ticket ticket);
    
    List<TicketSummaryDto> toSummaryDtoList(List<Ticket> tickets);

    @Mapping(target = "facilities", expression = "java(ticket.getBaseDetails().getFacilities())")
    @Mapping(target = "ticketCategoryDto", source = "ticketCategory")
    @Mapping(target = "matchSummaryDto", source = "match")
    TicketDetailsDto toDetailsDto(Ticket ticket);

    List<TicketDetailsDto> toDetailsDtoList(List<Ticket> tickets);
    
}
