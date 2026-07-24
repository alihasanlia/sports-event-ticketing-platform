package com.playtix.sports_event_ticketing_platform.domain.dto.ticket;

import java.math.BigDecimal;
import java.util.UUID;

public record TicketDetailsDto(
    UUID id,
    String seatNumber,
    String rowNumber,
    String sectionNumber,
    String tournomentName,
    String leagueName,
    String facilities,
    String stadiumName,
    BigDecimal price
) {}
