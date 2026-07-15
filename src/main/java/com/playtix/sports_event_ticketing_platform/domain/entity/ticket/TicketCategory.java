package com.playtix.sports_event_ticketing_platform.domain.entity.ticket;

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
@Table(name = "ticket_categories")
@Getter
@Setter
public class TicketCategory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String price;

    @Column(name = "remaining_capacity", nullable = false)
    private int remainingCapacity;
    
}
