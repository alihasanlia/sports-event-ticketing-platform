package com.playtix.sports_event_ticketing_platform.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.playtix.sports_event_ticketing_platform.domain.entity.members.User;
import com.playtix.sports_event_ticketing_platform.domain.entity.ticket.Ticket;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
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

@Entity
@Table(name = "ticket_cancellations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TicketCancellation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @Min(value = 0, message = "Penalty percent cannot be negative")
    @Max(value = 100, message = "Penalty percent cannot exceed 100")
    @Column(name = "penalty_percent", nullable = false, updatable = false)
    private Integer penaltyPercent;

    @NotNull(message = "Refund amount is required")
    @PositiveOrZero(message = "Refund amount must be positive or zero")
    @Column(name = "refund_amount", nullable = false, updatable = false, precision = 19, scale = 2)
    private BigDecimal refundAmount;

    @Column(name = "cancellation_fee", precision = 19, scale = 2)
    private BigDecimal cancellationFee;

    @Column(name = "request_date", nullable = false, updatable = false)
    private LocalDateTime requestDate;

    @Column(length = 500)
    private String reason;

    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull(message = "Ticket is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;
    
    @PrePersist
    protected void onCreate() {
        this.requestDate = LocalDateTime.now();
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