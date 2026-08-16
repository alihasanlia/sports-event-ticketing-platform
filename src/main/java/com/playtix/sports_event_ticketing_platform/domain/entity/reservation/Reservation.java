package com.playtix.sports_event_ticketing_platform.domain.entity.reservation;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import com.playtix.sports_event_ticketing_platform.domain.entity.Team;
import com.playtix.sports_event_ticketing_platform.domain.entity.match.Match;
import com.playtix.sports_event_ticketing_platform.domain.entity.members.User;
import com.playtix.sports_event_ticketing_platform.domain.entity.payment.Payment;
import com.playtix.sports_event_ticketing_platform.domain.entity.ticket.Ticket;
import com.playtix.sports_event_ticketing_platform.domain.entity.ticket.TicketCategory;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class Reservation {

    @EqualsAndHashCode.Include
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @NotNull(message = "Status is required")
    @Builder.Default
    private ReservationStatus status = ReservationStatus.PENDING;

    private LocalDateTime reservationDate;

    private LocalDateTime expiry;

    @Positive(message = "Quantity must be positive")
    @Builder.Default
    private int quantity = 1;

    private Payment payment;

    @NotNull(message = "Ticket is required")
    private Ticket ticket;

    @NotNull(message = "User is required")
    private User user;
    
    public void initializeDefaults() {
        this.reservationDate = LocalDateTime.now();
        if (this.expiry == null) {
            this.expiry = this.reservationDate.plusMinutes(30);
        }
        if (this.status == null) {
            this.status = ReservationStatus.PENDING;
        }
        if (this.quantity == 0) {
            this.quantity = 1;
        }
    }

    public void associatePayment(Payment payment) {
        this.payment = payment;
        if (payment != null) {
            payment.setReservation(this);
        }
    }

    public void disassociatePayment() {
        if (this.payment != null) {
            this.payment.setReservation(null);
            this.payment = null;
        }
    }

    private Duration getTimeRemaining() {
        if (this.expiry == null) {
            return Duration.ZERO;
        }
        
        LocalDateTime now = LocalDateTime.now();
        if (this.expiry.isBefore(now)) {
            return Duration.ZERO;
        }
        
        return Duration.between(now, this.expiry);
    }

    public String generateTimeRemainingString() {
        Duration remaining = getTimeRemaining();
        long minutes = remaining.toMinutes();
        long seconds = remaining.toSecondsPart();
        return "Time remaining: " + minutes + " minutes and " + seconds + " seconds";
    }
    
    public void confirm() {
        if (this.status != ReservationStatus.PENDING) {
            throw new IllegalStateException("Only pending reservations can be confirmed");
        }
        this.status = ReservationStatus.PAID;
    }

    public void cancel() {
        if (this.status == ReservationStatus.PAID) {
            throw new IllegalStateException("Confirmed reservations cannot be cancelled from this section!");
        }
        this.status = ReservationStatus.CANCELLED;
        if (this.ticket != null) {
            this.ticket.setReservation(null);
        }
    }

    public void expire() {
        if (this.status != ReservationStatus.PENDING) {
            throw new IllegalStateException("Only pending reservations can expire");
        }
        
        this.status = ReservationStatus.CANCELLED;
        
        if (this.ticket != null) {
            this.ticket.setReservation(null);
        }
    }

    public boolean isPending() {
        return this.status == ReservationStatus.PENDING;
    }

    public boolean isPaid() {
        return this.status == ReservationStatus.PAID;
    }

    public boolean isCancelled() {
        return this.status == ReservationStatus.CANCELLED;
    }

    public boolean isActive() {
        return this.status == ReservationStatus.PENDING && 
               this.expiry != null &&
               this.expiry.isAfter(LocalDateTime.now());
    }

    public boolean isExpiredNow() {
        return this.status == ReservationStatus.PENDING && 
               this.expiry != null &&
               this.expiry.isBefore(LocalDateTime.now());
    }

    public void checkAndExpire() {
        if (isExpiredNow() && this.ticket != null) {
            this.ticket.setReservation(null);
        }
    }

    public long getRemainingMinutes() {
        if (this.expiry == null || this.expiry.isBefore(LocalDateTime.now())) {
            return 0;
        }
        return java.time.Duration.between(LocalDateTime.now(), this.expiry).toMinutes();
    }

    public String getReservationInfo() {
        return String.format("Reservation %s - Ticket: %s - User: %s - Status: %s",
            id,
            ticket != null ? ticket.getId() : "null",
            user != null ? user.getEmail() : "null",
            status
        );
    }

    public Match getMatch() {
        if (this.ticket == null) return null;
        TicketCategory category = this.ticket.getTicketCategory();
        if (category == null) return null;
        return category.getMatch();
    }
    
    public String getMatchDescription() {
        Match match = getMatch();
        if (match == null) return "No match information";
        
        Team homeTeam = match.getHomeTeam();
        Team awayTeam = match.getAwayTeam();
        
        if (homeTeam == null || awayTeam == null) {
            return "Match details not available";
        }
        
        return homeTeam.getName() + " vs " + awayTeam.getName();
    }
    
    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", status=" + status +
                ", reservationDate=" + reservationDate +
                ", expiry=" + expiry +
                ", quantity=" + quantity +
                ", ticket=" + (ticket != null ? ticket.getId() : "null") +
                ", user=" + (user != null ? user.getEmail() : "null") +
                '}';
    }
}