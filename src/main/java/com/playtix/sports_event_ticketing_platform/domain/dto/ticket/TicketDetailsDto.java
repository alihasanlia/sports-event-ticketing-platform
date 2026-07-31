package com.playtix.sports_event_ticketing_platform.domain.dto.ticket;

import java.math.BigDecimal;
import java.util.UUID;

import com.playtix.sports_event_ticketing_platform.domain.dto.match.MatchSummaryDto;

public record TicketDetailsDto(
    UUID id,
    String seatNumber,
    String rowNumber,
    String sectionNumber,
    String facilities,
    BigDecimal price,
    TicketCategoryDto ticketCategoryDto,
    MatchSummaryDto matchSummaryDto
) {}
