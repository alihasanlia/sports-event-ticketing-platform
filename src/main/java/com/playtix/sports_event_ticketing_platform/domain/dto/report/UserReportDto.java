package com.playtix.sports_event_ticketing_platform.domain.dto.report;

import java.time.LocalDateTime;
import java.util.UUID;

import com.playtix.sports_event_ticketing_platform.domain.dto.ticket.TicketSummaryDto;
import com.playtix.sports_event_ticketing_platform.domain.entity.report.ReportStatus;
import com.playtix.sports_event_ticketing_platform.domain.entity.report.ReportSubject;

public record UserReportDto(
    UUID id,
    ReportSubject subject,
    String description,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    String adminResponse,
    ReportStatus status,
    TicketSummaryDto ticketSummaryDto
) {}
