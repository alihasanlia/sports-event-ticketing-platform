package com.playtix.sports_event_ticketing_platform.domain.dto.report;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.playtix.sports_event_ticketing_platform.domain.entity.report.ReportSubject;

import java.util.UUID;

public record CreateReportRequest(
    @NotNull(message = "User ID is required")
    UUID userId,
    
    @NotNull(message = "Ticket ID is required")
    UUID ticketId,
    
    @NotNull(message = "Subject is required")
    ReportSubject subject,
    
    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters")
    String description
) {}
