package com.playtix.sports_event_ticketing_platform.domain.entity.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.playtix.sports_event_ticketing_platform.domain.entity.members.User;
import com.playtix.sports_event_ticketing_platform.domain.entity.reservation.Reservation;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Payment {

    private UUID id = UUID.randomUUID();

    private BigDecimal amount;

    private PaymentMethod paymentMethod;

    private LocalDateTime paymentDate;

    private String eventName;

    private PaymentStatus status;

    private String transactionId;

    private String paymentReference;

    private String bankReceiptNumber;

    private String failureReason;

    private Reservation reservation;

    private User user;

    public void initializeDefaults() {
        this.paymentDate = LocalDateTime.now();
        if (this.status == null) {
            this.status = PaymentStatus.PENDING;
        }
        if (this.transactionId == null) {
            this.transactionId = generateTransactionId();
        }
        if (this.reservation != null && this.eventName == null) {
            this.eventName = reservation.getMatchDescription();
        }
    }

    public void markAsCompleted() {
        this.status = PaymentStatus.SUCCESSFUL;
        this.paymentDate = LocalDateTime.now();
    }

    public void markAsFailed(String reason) {
        this.status = PaymentStatus.UNSUCCESSFUL;
        this.failureReason = reason;
    }

    public void markAsRefunded() {
        this.status = PaymentStatus.REFUNDED;
    }

    public boolean isSuccessful() {
        return this.status == PaymentStatus.SUCCESSFUL;
    }

    public boolean isPending() {
        return this.status == PaymentStatus.PENDING;
    }

    private String generateTransactionId() {
        return "TXN-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8);
    }
}