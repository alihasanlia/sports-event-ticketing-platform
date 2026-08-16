package com.playtix.sports_event_ticketing_platform.service;

import com.playtix.sports_event_ticketing_platform.domain.dto.reservation.AdminReservationDto;
import com.playtix.sports_event_ticketing_platform.domain.dto.reservation.ConfirmReservationRequest;
import com.playtix.sports_event_ticketing_platform.domain.dto.reservation.CreateReservationRequest;
import com.playtix.sports_event_ticketing_platform.domain.dto.reservation.UserReservationDto;
import com.playtix.sports_event_ticketing_platform.domain.entity.members.User;
import com.playtix.sports_event_ticketing_platform.domain.entity.reservation.Reservation;
import com.playtix.sports_event_ticketing_platform.domain.entity.reservation.ReservationStatus;
import com.playtix.sports_event_ticketing_platform.domain.entity.ticket.Ticket;
import com.playtix.sports_event_ticketing_platform.domain.entity.ticket.TicketStatus;
import com.playtix.sports_event_ticketing_platform.mapper.ReservationMapper;
import com.playtix.sports_event_ticketing_platform.repository.ReservationRepository;
import com.playtix.sports_event_ticketing_platform.repository.TicketRepository;
import com.playtix.sports_event_ticketing_platform.repository.UserRepository;
import com.playtix.sports_event_ticketing_platform.service.redis.ReservationLockService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final ReservationMapper reservationMapper;
    private final ReservationLockService reservationLockService;

    @Transactional
    public UserReservationDto createReservation(CreateReservationRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Ticket ticket = ticketRepository.findById(request.ticketId())
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        if (ticket.getStatus() != TicketStatus.NOT_RESERVED) {
            throw new RuntimeException("Ticket is not available");
        }

        if (reservationRepository.existsActiveReservationForTicket(ticket.getId())) {
            throw new RuntimeException("Ticket is already reserved");
        }

        // Acquire distributed temporary lock in Redis (SETNX) with TTL
        boolean locked = reservationLockService.acquireLock(ticket.getId(), user.getId());
        if (!locked) {
            throw new RuntimeException("Ticket is temporarily locked by another user");
        }

        ticket.reserve();
        ticketRepository.save(ticket);

        Reservation reservation = Reservation.builder()
                .user(user)
                .ticket(ticket)
                .quantity(request.quantity())
                .status(ReservationStatus.PENDING)
                .expiry(LocalDateTime.now().plusMinutes(30))
                .build();

        reservation = reservationRepository.save(reservation);

        return reservationMapper.toUserReservationDto(reservation);
    }

    @Transactional
    public void cancelReservation(UUID reservationId, UUID userId) {
        Reservation reservation = reservationRepository.findByIdWithTicket(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        if (!reservation.getUser().getId().equals(userId)) {
            throw new RuntimeException("You don't have permission to cancel this reservation");
        }

        if (reservation.getStatus() == ReservationStatus.PAID) {
            throw new RuntimeException("Cannot cancel a paid reservation");
        }

        reservation.cancel();
        reservationRepository.save(reservation);
        // Release Redis reservation lock on cancellation
        reservationLockService.releaseLock(reservation.getTicket().getId(), userId);
    }

    @Transactional
    public UserReservationDto confirmReservation(ConfirmReservationRequest request) {
        Reservation reservation = reservationRepository.findByIdWithTicket(request.reservationId())
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new RuntimeException("Reservation is not pending");
        }

        if (reservation.isExpiredNow()) {
            throw new RuntimeException("Reservation has expired");
        }

        reservation.confirm();
        reservation = reservationRepository.save(reservation);
        // Release lock after confirmation
        reservationLockService.releaseLock(reservation.getTicket().getId(), reservation.getUser().getId());

        return reservationMapper.toUserReservationDto(reservation);
    }

    @Transactional
    public void expireReservation(UUID reservationId) {
        Reservation reservation = reservationRepository.findByIdWithTicket(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new RuntimeException("Only pending reservations can expire");
        }

        reservation.expire();
        reservationRepository.save(reservation);
        // Release lock on expiration
        reservationLockService.releaseLock(reservation.getTicket().getId(), reservation.getUser().getId());
    }

    @Transactional(readOnly = true)
    public UserReservationDto getUserReservation(UUID reservationId) {
        Reservation reservation = reservationRepository.findByIdWithTicket(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        return reservationMapper.toUserReservationDto(reservation);
    }

    @Transactional(readOnly = true)
    public AdminReservationDto getAdminReservation(UUID reservationId) {
        Reservation reservation = reservationRepository.findByIdWithAllRelationships(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        return reservationMapper.toAdminReservationDto(reservation);
    }

    @Transactional(readOnly = true)
    public List<UserReservationDto> getUserReservations(UUID userId) {
        List<Reservation> reservations = reservationRepository.findByUser_IdOrderByReservationDateDesc(userId);
        return reservationMapper.toUserReservationDtoList(reservations);
    }

    @Transactional(readOnly = true)
    public List<UserReservationDto> getUserActiveReservations(UUID userId) {
        List<Reservation> reservations = reservationRepository.findActivePendingReservationsByUserId(userId, LocalDateTime.now());
        return reservationMapper.toUserReservationDtoList(reservations);
    }

    @Transactional(readOnly = true)
    public List<UserReservationDto> getUserPaidReservations(UUID userId) {
        List<Reservation> reservations = reservationRepository.findPaidReservationsByUserId(userId);
        return reservationMapper.toUserReservationDtoList(reservations);
    }

    @Transactional(readOnly = true)
    public List<AdminReservationDto> getAllReservations() {
        List<Reservation> reservations = reservationRepository.findAll();
        return reservationMapper.toAdminReservationDtoList(reservations);
    }

    @Transactional(readOnly = true)
    public List<AdminReservationDto> getReservationsByStatus(ReservationStatus status) {
        List<Reservation> reservations = reservationRepository.findByStatus(status);
        return reservationMapper.toAdminReservationDtoList(reservations);
    }

    @Transactional(readOnly = true)
    public List<AdminReservationDto> getPendingReservations() {
        List<Reservation> reservations = reservationRepository.findByStatus(ReservationStatus.PENDING);
        return reservationMapper.toAdminReservationDtoList(reservations);
    }

    @Transactional(readOnly = true)
    public List<AdminReservationDto> getExpiredReservations() {
        List<Reservation> reservations = reservationRepository.findExpiredPendingReservations(LocalDateTime.now());
        return reservationMapper.toAdminReservationDtoList(reservations);
    }

    @Transactional
    public void cleanupExpiredReservations() {
        List<Reservation> expiredReservations = reservationRepository.findExpiredPendingReservations(LocalDateTime.now());
        for (Reservation reservation : expiredReservations) {
            reservation.expire();
            reservationRepository.save(reservation);
        }
    }
}