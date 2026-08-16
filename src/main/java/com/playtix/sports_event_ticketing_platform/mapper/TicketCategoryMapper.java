package com.playtix.sports_event_ticketing_platform.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.playtix.sports_event_ticketing_platform.domain.dto.ticket.TicketCategoryDto;
import com.playtix.sports_event_ticketing_platform.domain.entity.ticket.TicketCategory;

@Mapper(componentModel = "spring")
public interface TicketCategoryMapper {

    TicketCategoryDto toTicketCategoryDto(TicketCategory ticketCategory);

    List<TicketCategoryDto> toTicketCategoryDtoList(List<TicketCategory> ticketCategories);
    
}
