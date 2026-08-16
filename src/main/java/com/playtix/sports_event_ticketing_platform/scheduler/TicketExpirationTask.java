package com.playtix.sports_event_ticketing_platform.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.playtix.sports_event_ticketing_platform.domain.entity.reservation.Reservation;
import com.playtix.sports_event_ticketing_platform.domain.entity.ticket.Ticket;
import com.playtix.sports_event_ticketing_platform.repository.ReservationRepository;
import com.playtix.sports_event_ticketing_platform.repository.TicketRepository;

@Component
public class TicketExpirationTask {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void releaseExpiredTickets() {
        LocalDateTime now = LocalDateTime.now();
        
        List<Reservation> expiredReservations = reservationRepository.findExpiredPendingReservations(now);

        for (Reservation reservation : expiredReservations) {
            reservation.expire();
            reservationRepository.updateStatus(reservation.getId(), reservation.getStatus());
            Ticket ticket = reservation.getTicket();
            if (ticket != null) {
                ticket.cancelReservation();
                ticketRepository.save(ticket);
            }
        }
    }
}