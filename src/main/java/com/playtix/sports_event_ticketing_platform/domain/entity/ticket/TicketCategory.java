package com.playtix.sports_event_ticketing_platform.domain.entity.ticket;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.playtix.sports_event_ticketing_platform.domain.entity.match.Match;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class TicketCategory {

    @EqualsAndHashCode.Include
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @NotNull(message = "Category name is required")
    private Category category;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;

    @Positive(message = "Total capacity must be positive")
    private int totalCapacity;

    @PositiveOrZero(message = "Remaining capacity cannot be negative")
    private int remainingCapacity;

    private String description;

    @NotNull(message = "Match is required")
    private Match match;

    @Builder.Default
    private List<Ticket> tickets = new ArrayList<>();
    
    // متد جایگزین @PrePersist برای مقداردهی اولیه ظرفیت
    public void initializeDefaults() {
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