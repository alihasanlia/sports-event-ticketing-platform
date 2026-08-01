package com.playtix.sports_event_ticketing_platform.domain.dto.report;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.playtix.sports_event_ticketing_platform.domain.entity.report.ReportStatus;

import java.util.UUID;

public record UpdateReportRequest(
    @NotNull(message = "Report ID is required")
    UUID reportId,
    
    @NotBlank(message = "Response is required")
    @Size(max = 500, message = "Response cannot exceed 500 characters")
    String adminResponse,
    
    @NotNull(message = "Status is required")
    ReportStatus status
) {}
