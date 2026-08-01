package com.playtix.sports_event_ticketing_platform.repositories;

import com.playtix.sports_event_ticketing_platform.domain.entity.ticket.Ticket;
import com.playtix.sports_event_ticketing_platform.domain.entity.ticket.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {

    List<Ticket> findByStatus(TicketStatus status);

    List<Ticket> findByMatch_Id(UUID matchId);

    List<Ticket> findByMatch_IdAndStatus(UUID matchId, TicketStatus status);

    List<Ticket> findByTicketCategory_Id(UUID categoryId);

    List<Ticket> findByTicketCategory_IdAndStatus(UUID categoryId, TicketStatus status);

    Optional<Ticket> findByBarcode(String barcode);

    Optional<Ticket> findByQrCode(String qrCode);

    Optional<Ticket> findByEntryCode(String entryCode);

    List<Ticket> findBySeatNumber(String seatNumber);

    List<Ticket> findByMatch_IdAndSeatNumber(UUID matchId, String seatNumber);

    List<Ticket> findByMatch_IdAndSectionNumber(UUID matchId, String sectionNumber);

    List<Ticket> findByMatch_IdAndRowNumber(UUID matchId, String rowNumber);

    List<Ticket> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    List<Ticket> findByFinalPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    List<Ticket> findByPurchaseDateBetween(LocalDateTime start, LocalDateTime end);

    List<Ticket> findByMatch_IdAndStatusOrderBySeatNumberAsc(UUID matchId, TicketStatus status);

    List<Ticket> findByMatch_IdOrderBySeatNumberAsc(UUID matchId);

    long countByMatch_Id(UUID matchId);

    long countByMatch_IdAndStatus(UUID matchId, TicketStatus status);

    long countByTicketCategory_Id(UUID categoryId);

    @Query("SELECT t FROM Ticket t LEFT JOIN FETCH t.match LEFT JOIN FETCH t.ticketCategory WHERE t.id = :ticketId")
    Optional<Ticket> findByIdWithMatchAndCategory(@Param("ticketId") UUID ticketId);

    @Query("SELECT t FROM Ticket t LEFT JOIN FETCH t.reservation WHERE t.id = :ticketId")
    Optional<Ticket> findByIdWithReservation(@Param("ticketId") UUID ticketId);

    @Query("SELECT t FROM Ticket t LEFT JOIN FETCH t.baseDetails WHERE t.id = :ticketId")
    Optional<Ticket> findByIdWithBaseDetails(@Param("ticketId") UUID ticketId);

    @Query("SELECT t FROM Ticket t LEFT JOIN FETCH t.match LEFT JOIN FETCH t.ticketCategory LEFT JOIN FETCH t.reservation WHERE t.id = :ticketId")
    Optional<Ticket> findByIdWithAllRelationships(@Param("ticketId") UUID ticketId);

    @Query("SELECT t FROM Ticket t WHERE t.match.id = :matchId AND t.status = 'NOT_RESERVED'")
    List<Ticket> findAvailableTicketsByMatchId(@Param("matchId") UUID matchId);

    @Query("SELECT t FROM Ticket t WHERE t.match.id = :matchId AND t.status = 'RESERVED'")
    List<Ticket> findReservedTicketsByMatchId(@Param("matchId") UUID matchId);

    @Query("SELECT t FROM Ticket t WHERE t.match.id = :matchId AND t.status = 'SOLD'")
    List<Ticket> findSoldTicketsByMatchId(@Param("matchId") UUID matchId);

    @Query("SELECT t FROM Ticket t WHERE t.match.id = :matchId AND t.status IN ('RESERVED', 'SOLD')")
    List<Ticket> findUnavailableTicketsByMatchId(@Param("matchId") UUID matchId);

    @Query("SELECT t FROM Ticket t WHERE t.reservation.user.id = :userId")
    List<Ticket> findTicketsByUserId(@Param("userId") UUID userId);

    @Query("SELECT t FROM Ticket t WHERE t.reservation.user.id = :userId AND t.status = 'SOLD'")
    List<Ticket> findPurchasedTicketsByUserId(@Param("userId") UUID userId);

    @Query("SELECT t FROM Ticket t WHERE t.reservation.user.id = :userId AND t.status = 'RESERVED'")
    List<Ticket> findReservedTicketsByUserId(@Param("userId") UUID userId);

    @Query("SELECT t FROM Ticket t WHERE t.reservation.id = :reservationId")
    List<Ticket> findTicketsByReservationId(@Param("reservationId") UUID reservationId);

    @Query("SELECT t FROM Ticket t WHERE t.match.id = :matchId AND t.status = 'SOLD' AND t.purchaseDate >= :since")
    List<Ticket> findSoldTicketsByMatchIdSince(@Param("matchId") UUID matchId, @Param("since") LocalDateTime since);

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.match.id = :matchId AND t.status = 'SOLD'")
    long countSoldTicketsByMatchId(@Param("matchId") UUID matchId);

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.match.id = :matchId AND t.status = 'RESERVED'")
    long countReservedTicketsByMatchId(@Param("matchId") UUID matchId);

    @Query("SELECT SUM(t.finalPrice) FROM Ticket t WHERE t.match.id = :matchId AND t.status = 'SOLD'")
    BigDecimal sumFinalPriceByMatchId(@Param("matchId") UUID matchId);

    @Query("SELECT t.match.id, COUNT(t) FROM Ticket t WHERE t.status = 'SOLD' GROUP BY t.match.id")
    List<Object[]> countSoldTicketsByMatch();

    @Query("SELECT t.ticketCategory.id, COUNT(t) FROM Ticket t WHERE t.match.id = :matchId AND t.status = 'SOLD' GROUP BY t.ticketCategory.id")
    List<Object[]> countSoldTicketsByCategoryForMatch(@Param("matchId") UUID matchId);

    @Query("SELECT t FROM Ticket t WHERE t.match.id = :matchId AND t.status = 'NOT_RESERVED' AND t.seatNumber = :seatNumber")
    Optional<Ticket> findAvailableTicketByMatchAndSeat(@Param("matchId") UUID matchId, @Param("seatNumber") String seatNumber);

    @Query("SELECT t FROM Ticket t WHERE t.barcode IS NOT NULL AND t.status = 'SOLD'")
    List<Ticket> findSoldTicketsWithBarcode();

    @Query("SELECT t FROM Ticket t WHERE t.qrCode IS NOT NULL AND t.status = 'SOLD'")
    List<Ticket> findSoldTicketsWithQrCode();

    boolean existsByBarcode(String barcode);

    boolean existsByQrCode(String qrCode);

    boolean existsByEntryCode(String entryCode);

    @Query("SELECT t FROM Ticket t WHERE t.status = 'RESERVED' AND t.reservation.reservationTime <= :expiryTime")
    List<Ticket> findExpiredReservations(@Param("expiryTime") LocalDateTime expiryTime);
}
