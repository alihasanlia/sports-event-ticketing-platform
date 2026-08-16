package com.playtix.sports_event_ticketing_platform.domain.dto.report;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record AssignReportRequest(
    @NotNull(message = "Report ID is required")
    UUID reportId,
    
    @NotNull(message = "Support ID is required")
    UUID supportId
) {}