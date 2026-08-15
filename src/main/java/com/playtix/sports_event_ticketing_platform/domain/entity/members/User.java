package com.playtix.sports_event_ticketing_platform.domain.entity.members;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.playtix.sports_event_ticketing_platform.domain.entity.TicketCancellation;
import com.playtix.sports_event_ticketing_platform.domain.entity.payment.Payment;
import com.playtix.sports_event_ticketing_platform.domain.entity.report.Report;
import com.playtix.sports_event_ticketing_platform.domain.entity.reservation.Reservation;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User extends BaseUser {

    public User() {
        this.setRole(Role.USER);
        this.reports = new ArrayList<>();
        this.canceledTickets = new ArrayList<>();
        this.payments = new ArrayList<>();
        this.reservedTickets = new ArrayList<>();
    }

    private List<Report> reports;
    private List<TicketCancellation> canceledTickets;
    private List<Payment> payments;
    private List<Reservation> reservedTickets;
    private BigDecimal balance;

    public void addBalance(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        this.balance = this.balance.add(amount);
    }
    
    public void deductBalance(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (this.balance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }
        this.balance = this.balance.subtract(amount);
    }

    public void addReport(Report report) {
        reports.add(report);
    }

    public void removeReport(Report report) {
        reports.remove(report);
    }

    public void addPayment(Payment payment) {
        payments.add(payment);
    }

    public void addReservation(Reservation reservation) {
        reservedTickets.add(reservation);
    }

    public void cancelTicket(TicketCancellation cancellation) {
        canceledTickets.add(cancellation);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + getId() +
                ", firstname='" + getFirstname() + '\'' +
                ", lastname='" + getLastname() + '\'' +
                ", phone='" + getPhoneNumber() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", role=" + getRole() +
                '}';
    }
}