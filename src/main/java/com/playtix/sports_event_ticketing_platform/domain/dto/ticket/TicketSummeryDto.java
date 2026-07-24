package com.playtix.sports_event_ticketing_platform.domain.dto.ticket;

import java.math.BigDecimal;
import java.util.UUID;

public record TicketSummeryDto(
    UUID id,
    String seatNumber,
    String rowNumber,
    String sectionNumber,
    BigDecimal price
) {}
