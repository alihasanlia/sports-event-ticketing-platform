package com.playtix.sports_event_ticketing_platform.repository;

import com.playtix.sports_event_ticketing_platform.domain.entity.reservation.Reservation;
import com.playtix.sports_event_ticketing_platform.domain.entity.reservation.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    List<Reservation> findByStatus(ReservationStatus status);

    List<Reservation> findByUser_Id(UUID userId);

    List<Reservation> findByUser_IdAndStatus(UUID userId, ReservationStatus status);

    List<Reservation> findByTicket_Id(UUID ticketId);

    Optional<Reservation> findByPayment_Id(UUID paymentId);

    List<Reservation> findByReservationDateBetween(LocalDateTime start, LocalDateTime end);

    List<Reservation> findByReservationDateAfter(LocalDateTime date);

    List<Reservation> findByReservationDateBefore(LocalDateTime date);

    List<Reservation> findByExpiryBefore(LocalDateTime expiry);

    List<Reservation> findByExpiryAfter(LocalDateTime expiry);

    List<Reservation> findByUser_IdOrderByReservationDateDesc(UUID userId);

    List<Reservation> findByStatusOrderByReservationDateAsc(ReservationStatus status);

    @Query("SELECT r FROM Reservation r LEFT JOIN FETCH r.user LEFT JOIN FETCH r.ticket LEFT JOIN FETCH r.payment WHERE r.id = :reservationId")
    Optional<Reservation> findByIdWithAllRelationships(@Param("reservationId") UUID reservationId);

    @Query("SELECT r FROM Reservation r LEFT JOIN FETCH r.user LEFT JOIN FETCH r.ticket WHERE r.id = :reservationId")
    Optional<Reservation> findByIdWithUserAndTicket(@Param("reservationId") UUID reservationId);

    @Query("SELECT r FROM Reservation r LEFT JOIN FETCH r.payment WHERE r.id = :reservationId")
    Optional<Reservation> findByIdWithPayment(@Param("reservationId") UUID reservationId);

    @Query("SELECT r FROM Reservation r LEFT JOIN FETCH r.ticket WHERE r.id = :reservationId")
    Optional<Reservation> findByIdWithTicket(@Param("reservationId") UUID reservationId);

    @Query("SELECT r FROM Reservation r LEFT JOIN FETCH r.user WHERE r.id = :reservationId")
    Optional<Reservation> findByIdWithUser(@Param("reservationId") UUID reservationId);

    @Query("SELECT r FROM Reservation r WHERE r.status = 'PENDING' AND r.expiry < :now")
    List<Reservation> findExpiredPendingReservations(@Param("now") LocalDateTime now);

    @Query("SELECT r FROM Reservation r WHERE r.status = 'PENDING' AND r.expiry > :now")
    List<Reservation> findActivePendingReservations(@Param("now") LocalDateTime now);

    @Query("SELECT r FROM Reservation r WHERE r.user.id = :userId AND r.status = 'PENDING' AND r.expiry > :now")
    List<Reservation> findActivePendingReservationsByUserId(@Param("userId") UUID userId, @Param("now") LocalDateTime now);

    @Query("SELECT r FROM Reservation r WHERE r.user.id = :userId AND r.status = 'PAID'")
    List<Reservation> findPaidReservationsByUserId(@Param("userId") UUID userId);

    @Query("SELECT r FROM Reservation r WHERE r.user.id = :userId AND r.status = 'CANCELLED'")
    List<Reservation> findCancelledReservationsByUserId(@Param("userId") UUID userId);

    @Query("SELECT r FROM Reservation r WHERE r.ticket.match.id = :matchId")
    List<Reservation> findByMatchId(@Param("matchId") UUID matchId);

    @Query("SELECT r FROM Reservation r WHERE r.ticket.match.id = :matchId AND r.status = 'PAID'")
    List<Reservation> findPaidReservationsByMatchId(@Param("matchId") UUID matchId);

    @Query("SELECT r FROM Reservation r WHERE r.ticket.match.id = :matchId AND r.status = 'PENDING' AND r.expiry > :now")
    List<Reservation> findActivePendingReservationsByMatchId(@Param("matchId") UUID matchId, @Param("now") LocalDateTime now);

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.ticket.match.id = :matchId AND r.status = 'PAID'")
    long countPaidReservationsByMatchId(@Param("matchId") UUID matchId);

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.ticket.match.id = :matchId AND r.status = 'PENDING'")
    long countPendingReservationsByMatchId(@Param("matchId") UUID matchId);

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.user.id = :userId AND r.status = 'PAID'")
    long countPaidReservationsByUserId(@Param("userId") UUID userId);

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.user.id = :userId AND r.status = 'PENDING' AND r.expiry > :now")
    long countActivePendingReservationsByUserId(@Param("userId") UUID userId, @Param("now") LocalDateTime now);

    @Query("SELECT r.user.id, COUNT(r) FROM Reservation r WHERE r.status = 'PAID' GROUP BY r.user.id ORDER BY COUNT(r) DESC")
    List<Object[]> countPaidReservationsByUser();

    @Query("SELECT r.ticket.match.id, COUNT(r) FROM Reservation r WHERE r.status = 'PAID' GROUP BY r.ticket.match.id ORDER BY COUNT(r) DESC")
    List<Object[]> countPaidReservationsByMatch();

    @Query("SELECT r.status, COUNT(r) FROM Reservation r GROUP BY r.status")
    List<Object[]> countReservationsByStatus();

    @Query("SELECT FUNCTION('DATE', r.reservationDate), COUNT(r) FROM Reservation r GROUP BY FUNCTION('DATE', r.reservationDate) ORDER BY FUNCTION('DATE', r.reservationDate) DESC")
    List<Object[]> countReservationsByDay();

    @Query("SELECT r FROM Reservation r WHERE r.payment IS NULL AND r.status = 'PAID'")
    List<Reservation> findPaidReservationsWithoutPayment();

    @Query("SELECT r FROM Reservation r WHERE r.payment IS NOT NULL AND r.status = 'PENDING'")
    List<Reservation> findPendingReservationsWithPayment();

    @Query("SELECT r FROM Reservation r WHERE r.user.id = :userId AND r.ticket.id = :ticketId")
    Optional<Reservation> findByUserIdAndTicketId(@Param("userId") UUID userId, @Param("ticketId") UUID ticketId);

    @Query("SELECT r FROM Reservation r WHERE r.ticket.id = :ticketId AND r.status = 'PENDING'")
    Optional<Reservation> findPendingReservationByTicketId(@Param("ticketId") UUID ticketId);

    @Query("SELECT r FROM Reservation r WHERE r.ticket.id = :ticketId AND r.status = 'PAID'")
    Optional<Reservation> findPaidReservationByTicketId(@Param("ticketId") UUID ticketId);

    @Query("SELECT r FROM Reservation r WHERE r.user.id = :userId AND r.reservationDate >= :startDate")
    List<Reservation> findByUserIdAndDateAfter(@Param("userId") UUID userId, @Param("startDate") LocalDateTime startDate);

    @Query("SELECT r FROM Reservation r WHERE r.user.id = :userId AND r.reservationDate <= :endDate")
    List<Reservation> findByUserIdAndDateBefore(@Param("userId") UUID userId, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT r FROM Reservation r WHERE r.user.id = :userId AND r.reservationDate BETWEEN :start AND :end")
    List<Reservation> findByUserIdAndDateRange(@Param("userId") UUID userId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT r FROM Reservation r WHERE r.status = 'PENDING' AND r.expiry BETWEEN :start AND :end")
    List<Reservation> findPendingReservationsExpiringBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    boolean existsByTicket_IdAndStatusIn(UUID ticketId, List<ReservationStatus> statuses);

    @Query("SELECT COUNT(r) > 0 FROM Reservation r WHERE r.ticket.id = :ticketId AND r.status IN ('PENDING', 'PAID')")
    boolean existsActiveReservationForTicket(@Param("ticketId") UUID ticketId);

    @Query("SELECT r FROM Reservation r WHERE r.status = 'PENDING' AND r.quantity > 1")
    List<Reservation> findPendingReservationsWithMultipleTickets();
    
}