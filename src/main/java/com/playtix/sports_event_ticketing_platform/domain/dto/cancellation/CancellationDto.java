package com.playtix.sports_event_ticketing_platform.domain.dto.cancellation;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.playtix.sports_event_ticketing_platform.domain.dto.ticket.TicketSummaryDto;

public record CancellationDto(
    UUID id,
    int penaltyPercent,
    BigDecimal refundAmount,
    BigDecimal cancellationFee,
    LocalDateTime requestDate,
    TicketSummaryDto ticketSummaryDto
) {}
