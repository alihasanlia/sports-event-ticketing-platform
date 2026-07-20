package com.playtix.sports_event_ticketing_platform.domain.entity.members;

import java.util.ArrayList;
import java.util.List;

import com.playtix.sports_event_ticketing_platform.domain.entity.TicketCancellation;
import com.playtix.sports_event_ticketing_platform.domain.entity.payment.Payment;
import com.playtix.sports_event_ticketing_platform.domain.entity.report.Report;
import com.playtix.sports_event_ticketing_platform.domain.entity.reservation.Reservation;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Report> reports;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TicketCancellation> canceledTickets;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reservation> reservedTickets;

    public void addReport(Report report) {
        reports.add(report);
        report.setUser(this);
    }

    public void removeReport(Report report) {
        reports.remove(report);
        report.setUser(null);
    }

    public void addPayment(Payment payment) {
        payments.add(payment);
        payment.setUser(this);
    }

    public void addReservation(Reservation reservation) {
        reservedTickets.add(reservation);
        reservation.setUser(this);
    }

    public void cancelTicket(TicketCancellation cancellation) {
        canceledTickets.add(cancellation);
        cancellation.setUser(this);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + getId() +
                ", firstname=" + getFirstname() + '\'' +
                ", lastname=" + getLastname() + '\'' +
                ", phone='" + getPhoneNumber() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", role=" + getRole() +
                '}';
    }
}