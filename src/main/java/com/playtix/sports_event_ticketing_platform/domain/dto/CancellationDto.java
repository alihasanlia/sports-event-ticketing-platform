package com.playtix.sports_event_ticketing_platform.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.playtix.sports_event_ticketing_platform.domain.dto.ticket.TicketSummeryDto;

public record CancellationDto(
    UUID id,
    int penaltyPercent,
    BigDecimal refundAmount,
    BigDecimal cancellationFee,
    LocalDateTime requestDate,
    TicketSummeryDto ticketSummeryDto
) {}
