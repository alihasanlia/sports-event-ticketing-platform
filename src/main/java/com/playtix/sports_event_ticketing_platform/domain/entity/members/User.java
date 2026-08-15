package com.playtix.sports_event_ticketing_platform.domain.entity.members;

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