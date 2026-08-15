package com.playtix.sports_event_ticketing_platform.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.playtix.sports_event_ticketing_platform.domain.entity.members.User;
import com.playtix.sports_event_ticketing_platform.domain.entity.ticket.Ticket;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TicketCancellation {

    @EqualsAndHashCode.Include
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @Min(value = 0, message = "Penalty percent cannot be negative")
    @Max(value = 100, message = "Penalty percent cannot exceed 100")
    private int penaltyPercent;

    @NotNull(message = "Refund amount is required")
    @PositiveOrZero(message = "Refund amount must be positive or zero")
    private BigDecimal refundAmount;

    private BigDecimal cancellationFee;

    private LocalDateTime requestDate;

    private String reason;

    @NotNull(message = "User is required")
    private User user;

    @NotNull(message = "Ticket is required")
    private Ticket ticket;
    
    // متد جایگزین @PrePersist برای مقداردهی اولیه
    public void initializeDefaults() {
        if (this.requestDate == null) {
            this.requestDate = LocalDateTime.now();
        }
    }

    public BigDecimal getFinalRefundAmount() {
        if (refundAmount == null) {
            return BigDecimal.ZERO;
        }
        if (cancellationFee == null) {
            return refundAmount;
        }
        return refundAmount.subtract(cancellationFee);
    }
    
    @Override
    public String toString() {
        return "TicketCancellation{" +
                "id=" + id +
                ", penaltyPercent=" + penaltyPercent +
                ", refundAmount=" + refundAmount +
                ", requestDate=" + requestDate +
                ", user=" + (user != null ? user.getEmail() : "null") +
                ", ticket=" + (ticket != null ? ticket.getId() : "null") +
                '}';
    }
}