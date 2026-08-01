package com.playtix.sports_event_ticketing_platform.repository;

import com.playtix.sports_event_ticketing_platform.domain.entity.TicketCancellation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TicketCancellationRepository extends JpaRepository<TicketCancellation, UUID> {

    List<TicketCancellation> findByUser_Id(UUID userId);

    List<TicketCancellation> findByTicket_Id(UUID ticketId);

    List<TicketCancellation> findByUser_IdOrderByRequestDateDesc(UUID userId);

    List<TicketCancellation> findByRequestDateBetween(LocalDateTime start, LocalDateTime end);

    List<TicketCancellation> findByRequestDateAfter(LocalDateTime date);

    List<TicketCancellation> findByRequestDateBefore(LocalDateTime date);

    List<TicketCancellation> findByPenaltyPercent(int penaltyPercent);

    List<TicketCancellation> findByPenaltyPercentBetween(int minPenalty, int maxPenalty);

    List<TicketCancellation> findByRefundAmountGreaterThan(BigDecimal amount);

    List<TicketCancellation> findByRefundAmountLessThan(BigDecimal amount);

    @Query("SELECT tc FROM TicketCancellation tc LEFT JOIN FETCH tc.user LEFT JOIN FETCH tc.ticket WHERE tc.id = :cancellationId")
    List<TicketCancellation> findByIdWithUserAndTicket(@Param("cancellationId") UUID cancellationId);

    @Query("SELECT tc FROM TicketCancellation tc LEFT JOIN FETCH tc.user WHERE tc.id = :cancellationId")
    List<TicketCancellation> findByIdWithUser(@Param("cancellationId") UUID cancellationId);

    @Query("SELECT tc FROM TicketCancellation tc LEFT JOIN FETCH tc.ticket WHERE tc.id = :cancellationId")
    List<TicketCancellation> findByIdWithTicket(@Param("cancellationId") UUID cancellationId);

    @Query("SELECT tc FROM TicketCancellation tc WHERE tc.user.id = :userId AND tc.requestDate >= :startDate")
    List<TicketCancellation> findByUserIdAndDateAfter(@Param("userId") UUID userId, @Param("startDate") LocalDateTime startDate);

    @Query("SELECT tc FROM TicketCancellation tc WHERE tc.user.id = :userId AND tc.requestDate <= :endDate")
    List<TicketCancellation> findByUserIdAndDateBefore(@Param("userId") UUID userId, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT tc FROM TicketCancellation tc WHERE tc.ticket.match.id = :matchId")
    List<TicketCancellation> findByMatchId(@Param("matchId") UUID matchId);

    @Query("SELECT tc FROM TicketCancellation tc WHERE tc.ticket.match.id = :matchId AND tc.requestDate BETWEEN :start AND :end")
    List<TicketCancellation> findByMatchIdAndDateRange(@Param("matchId") UUID matchId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT SUM(tc.refundAmount) FROM TicketCancellation tc WHERE tc.user.id = :userId")
    BigDecimal sumRefundAmountByUserId(@Param("userId") UUID userId);

    @Query("SELECT SUM(tc.refundAmount) FROM TicketCancellation tc WHERE tc.ticket.match.id = :matchId")
    BigDecimal sumRefundAmountByMatchId(@Param("matchId") UUID matchId);

    @Query("SELECT SUM(tc.refundAmount) FROM TicketCancellation tc WHERE tc.requestDate BETWEEN :start AND :end")
    BigDecimal sumRefundAmountByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT AVG(tc.penaltyPercent) FROM TicketCancellation tc")
    Double averagePenaltyPercent();

    @Query("SELECT AVG(tc.penaltyPercent) FROM TicketCancellation tc WHERE tc.user.id = :userId")
    Double averagePenaltyPercentByUserId(@Param("userId") UUID userId);

    @Query("SELECT COUNT(tc) FROM TicketCancellation tc WHERE tc.user.id = :userId")
    long countByUserId(@Param("userId") UUID userId);

    @Query("SELECT COUNT(tc) FROM TicketCancellation tc WHERE tc.ticket.match.id = :matchId")
    long countByMatchId(@Param("matchId") UUID matchId);

    @Query("SELECT tc.user.id, COUNT(tc) FROM TicketCancellation tc GROUP BY tc.user.id ORDER BY COUNT(tc) DESC")
    List<Object[]> countCancellationsByUser();

    @Query("SELECT tc.ticket.match.id, COUNT(tc) FROM TicketCancellation tc GROUP BY tc.ticket.match.id ORDER BY COUNT(tc) DESC")
    List<Object[]> countCancellationsByMatch();

    @Query("SELECT tc.ticket.id, COUNT(tc) FROM TicketCancellation tc GROUP BY tc.ticket.id ORDER BY COUNT(tc) DESC")
    List<Object[]> countCancellationsByTicket();

    @Query("SELECT FUNCTION('DATE', tc.requestDate), COUNT(tc) FROM TicketCancellation tc GROUP BY FUNCTION('DATE', tc.requestDate) ORDER BY FUNCTION('DATE', tc.requestDate) DESC")
    List<Object[]> countCancellationsByDay();

    @Query("SELECT FUNCTION('MONTH', tc.requestDate), FUNCTION('YEAR', tc.requestDate), COUNT(tc) FROM TicketCancellation tc GROUP BY FUNCTION('MONTH', tc.requestDate), FUNCTION('YEAR', tc.requestDate) ORDER BY FUNCTION('YEAR', tc.requestDate) DESC, FUNCTION('MONTH', tc.requestDate) DESC")
    List<Object[]> countCancellationsByMonth();

    @Query("SELECT tc FROM TicketCancellation tc WHERE tc.user.id = :userId AND tc.refundAmount > :amount")
    List<TicketCancellation> findByUserIdAndRefundAmountGreaterThan(@Param("userId") UUID userId, @Param("amount") BigDecimal amount);

    @Query("SELECT tc FROM TicketCancellation tc WHERE tc.ticket.id = :ticketId ORDER BY tc.requestDate DESC")
    List<TicketCancellation> findByTicketIdOrderByRequestDateDesc(@Param("ticketId") UUID ticketId);

    @Query("SELECT tc FROM TicketCancellation tc WHERE tc.user.id = :userId AND tc.ticket.id = :ticketId")
    List<TicketCancellation> findByUserIdAndTicketId(@Param("userId") UUID userId, @Param("ticketId") UUID ticketId);

    @Query("SELECT tc FROM TicketCancellation tc WHERE tc.cancellationFee IS NOT NULL")
    List<TicketCancellation> findWithCancellationFee();

    @Query("SELECT tc FROM TicketCancellation tc WHERE tc.cancellationFee IS NULL")
    List<TicketCancellation> findWithoutCancellationFee();

    @Query("SELECT tc FROM TicketCancellation tc WHERE tc.penaltyPercent = 0")
    List<TicketCancellation> findFullRefundCancellations();

    @Query("SELECT tc FROM TicketCancellation tc WHERE tc.penaltyPercent > 0")
    List<TicketCancellation> findPartialRefundCancellations();

    boolean existsByTicket_Id(UUID ticketId);

    @Query("SELECT COUNT(tc) > 0 FROM TicketCancellation tc WHERE tc.ticket.id = :ticketId")
    boolean hasCancellationForTicket(@Param("ticketId") UUID ticketId);
}
