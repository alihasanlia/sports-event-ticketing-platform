package com.playtix.sports_event_ticketing_platform.service.cancellation;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class CancellationEligibility {
    private boolean eligible;
    private String reason;
    private Integer penaltyPercent;
    private BigDecimal refundAmount;
    private BigDecimal originalPrice;
    private Long daysUntilMatch;
    private Long hoursUntilMatch;
    private LocalDateTime matchDateTime;
    private Integer refundPercentage;
}
