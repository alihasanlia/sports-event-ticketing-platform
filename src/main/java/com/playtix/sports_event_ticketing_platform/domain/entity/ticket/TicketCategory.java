package com.playtix.sports_event_ticketing_platform.domain.entity.ticket;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.playtix.sports_event_ticketing_platform.domain.entity.match.Match;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ticket_categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TicketCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @NotNull(message = "Category name is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    @Positive(message = "Total capacity must be positive")
    @Column(name = "total_capacity", nullable = false)
    private int totalCapacity;

    @PositiveOrZero(message = "Remaining capacity cannot be negative")
    @Column(name = "remaining_capacity", nullable = false)
    private int remainingCapacity;

    @Column(length = 500)
    private String description;

    @NotNull(message = "Match is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @OneToMany(mappedBy = "ticketCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Ticket> tickets = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        if (this.remainingCapacity == 0 && this.totalCapacity > 0) {
            this.remainingCapacity = this.totalCapacity;
        }
    }
    
    public void addTicket(Ticket ticket) {
        if (ticket != null && !tickets.contains(ticket)) {
            tickets.add(ticket);
            ticket.setTicketCategory(this);
        }
    }

    public void removeTicket(Ticket ticket) {
        if (ticket != null && tickets.remove(ticket)) {
            ticket.setTicketCategory(null);
        }
    }
    
    public boolean isAvailable() {
        return remainingCapacity > 0;
    }

    public int getSoldCount() {
        return totalCapacity - remainingCapacity;
    }

    public double getSoldPercentage() {
        if (totalCapacity == 0) return 0;
        return (double) getSoldCount() / totalCapacity * 100;
    }

    public void reserveTicket() {
        if (!isAvailable()) {
            throw new IllegalStateException("No tickets available in this category");
        }
        this.remainingCapacity--;
    }

    public void cancelReservation() {
        if (this.remainingCapacity >= this.totalCapacity) {
            throw new IllegalStateException("Cannot cancel reservation: no tickets reserved");
        }
        this.remainingCapacity++;
    }
    
    @Override
    public String toString() {
        return "TicketCategory{" +
                "id=" + id +
                ", category='" + category + '\'' +
                ", price=" + price +
                ", totalCapacity=" + totalCapacity +
                ", remainingCapacity=" + remainingCapacity +
                ", match=" + (match != null ? match.getId() : "null") +
                '}';
    }
}