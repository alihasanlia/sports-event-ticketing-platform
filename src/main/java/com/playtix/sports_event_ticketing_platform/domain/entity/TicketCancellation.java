package com.playtix.sports_event_ticketing_platform.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ticket_cancellations")
@Getter
@Setter
public class TicketCancellation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "penalty_percent", nullable = false, updatable = false)
    private int penaltyPercent;

    @Column(name = "refund_amount", nullable = false, updatable = false)
    private String refundAmount;

    @Column(nullable = false, updatable = false)
    private LocalDateTime requestDate;
    
}
