package com.playtix.sports_event_ticketing_platform.repository;

import com.playtix.sports_event_ticketing_platform.domain.entity.reservation.Reservation;
import com.playtix.sports_event_ticketing_platform.domain.entity.reservation.ReservationStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ReservationRepository {

    private final JdbcTemplate jdbcTemplate;

    public ReservationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Reservation> reservationRowMapper = (rs, rowNum) -> {
        Reservation reservation = new Reservation();
        reservation.setId(UUID.fromString(rs.getString("id")));
        
        String statusStr = rs.getString("status");
        if (statusStr != null) {
            reservation.setStatus(ReservationStatus.valueOf(statusStr));
        }
        
        Timestamp resDate = rs.getTimestamp("reservation_date");
        if (resDate != null) {
            reservation.setReservationDate(resDate.toLocalDateTime());
        }
        
        Timestamp expiry = rs.getTimestamp("expiry");
        if (expiry != null) {
            reservation.setExpiry(expiry.toLocalDateTime());
        }
        
        reservation.setQuantity(rs.getInt("quantity"));
        
        // Note: For full domain mapping, related entities can be mapped or loaded via custom queries if needed.
        return reservation;
    };

    // --- Standard CRUD Methods ---

    public List<Reservation> findAll() {
        String sql = "SELECT * FROM reservations";
        return jdbcTemplate.query(sql, reservationRowMapper);
    }

    public Optional<Reservation> findById(UUID id) {
        String sql = "SELECT * FROM reservations WHERE id = ?";
        List<Reservation> results = jdbcTemplate.query(sql, reservationRowMapper, id);
        return results.stream().findFirst();
    }

    public Reservation save(Reservation reservation) {
        if (findById(reservation.getId()).isPresent()) {
            String sql = "UPDATE reservations SET status = ?, expiry = ?, quantity = ?, payment_id = ?, ticket_id = ?, user_id = ? WHERE id = ?";
            jdbcTemplate.update(sql,
                    reservation.getStatus().name(),
                    reservation.getExpiry() != null ? Timestamp.valueOf(reservation.getExpiry()) : null,
                    reservation.getQuantity(),
                    reservation.getPayment() != null ? reservation.getPayment().getId() : null,
                    reservation.getTicket() != null ? reservation.getTicket().getId() : null,
                    reservation.getUser() != null ? reservation.getUser().getId() : null,
                    reservation.getId()
            );
        } else {
            String sql = "INSERT INTO reservations (id, status, reservation_date, expiry, quantity, payment_id, ticket_id, user_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            if (reservation.getId() == null) {
                reservation.setId(UUID.randomUUID());
            }
            if (reservation.getReservationDate() == null) {
                reservation.setReservationDate(LocalDateTime.now());
            }
            if (reservation.getExpiry() == null) {
                reservation.setExpiry(reservation.getReservationDate().plusMinutes(30));
            }
            jdbcTemplate.update(sql,
                    reservation.getId(),
                    reservation.getStatus() != null ? reservation.getStatus().name() : ReservationStatus.PENDING.name(),
                    Timestamp.valueOf(reservation.getReservationDate()),
                    Timestamp.valueOf(reservation.getExpiry()),
                    reservation.getQuantity(),
                    reservation.getPayment() != null ? reservation.getPayment().getId() : null,
                    reservation.getTicket() != null ? reservation.getTicket().getId() : null,
                    reservation.getUser() != null ? reservation.getUser().getId() : null
            );
        }
        return reservation;
    }

    public void deleteById(UUID id) {
        String sql = "DELETE FROM reservations WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public void delete(Reservation reservation) {
        if (reservation != null && reservation.getId() != null) {
            deleteById(reservation.getId());
        }
    }

    // --- Interface Specific Query Methods ---

    public List<Reservation> findByStatus(ReservationStatus status) {
        String sql = "SELECT * FROM reservations WHERE status = ?";
        return jdbcTemplate.query(sql, reservationRowMapper, status.name());
    }

    public List<Reservation> findByUser_Id(UUID userId) {
        String sql = "SELECT * FROM reservations WHERE user_id = ?";
        return jdbcTemplate.query(sql, reservationRowMapper, userId);
    }

    public List<Reservation> findByUser_IdAndStatus(UUID userId, ReservationStatus status) {
        String sql = "SELECT * FROM reservations WHERE user_id = ? AND status = ?";
        return jdbcTemplate.query(sql, reservationRowMapper, userId, status.name());
    }

    public List<Reservation> findByTicket_Id(UUID ticketId) {
        String sql = "SELECT * FROM reservations WHERE ticket_id = ?";
        return jdbcTemplate.query(sql, reservationRowMapper, ticketId);
    }

    public Optional<Reservation> findByPayment_Id(UUID paymentId) {
        String sql = "SELECT * FROM reservations WHERE payment_id = ?";
        List<Reservation> results = jdbcTemplate.query(sql, reservationRowMapper, paymentId);
        return results.stream().findFirst();
    }

    public List<Reservation> findByReservationDateBetween(LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT * FROM reservations WHERE reservation_date BETWEEN ? AND ?";
        return jdbcTemplate.query(sql, reservationRowMapper, Timestamp.valueOf(start), Timestamp.valueOf(end));
    }

    public List<Reservation> findByReservationDateAfter(LocalDateTime date) {
        String sql = "SELECT * FROM reservations WHERE reservation_date > ?";
        return jdbcTemplate.query(sql, reservationRowMapper, Timestamp.valueOf(date));
    }

    public List<Reservation> findByReservationDateBefore(LocalDateTime date) {
        String sql = "SELECT * FROM reservations WHERE reservation_date < ?";
        return jdbcTemplate.query(sql, reservationRowMapper, Timestamp.valueOf(date));
    }

    public List<Reservation> findByExpiryBefore(LocalDateTime expiry) {
        String sql = "SELECT * FROM reservations WHERE expiry < ?";
        return jdbcTemplate.query(sql, reservationRowMapper, Timestamp.valueOf(expiry));
    }

    public List<Reservation> findByExpiryAfter(LocalDateTime expiry) {
        String sql = "SELECT * FROM reservations WHERE expiry > ?";
        return jdbcTemplate.query(sql, reservationRowMapper, Timestamp.valueOf(expiry));
    }

    public List<Reservation> findByUser_IdOrderByReservationDateDesc(UUID userId) {
        String sql = "SELECT * FROM reservations WHERE user_id = ? ORDER BY reservation_date DESC";
        return jdbcTemplate.query(sql, reservationRowMapper, userId);
    }

    public List<Reservation> findByStatusOrderByReservationDateAsc(ReservationStatus status) {
        String sql = "SELECT * FROM reservations WHERE status = ? ORDER BY reservation_date ASC";
        return jdbcTemplate.query(sql, reservationRowMapper, status.name());
    }

    public Optional<Reservation> findByIdWithAllRelationships(UUID reservationId) {
        String sql = "SELECT * FROM reservations WHERE id = ?";
        return jdbcTemplate.query(sql, reservationRowMapper, reservationId).stream().findFirst();
    }

    public Optional<Reservation> findByIdWithUserAndTicket(UUID reservationId) {
        String sql = "SELECT * FROM reservations WHERE id = ?";
        return jdbcTemplate.query(sql, reservationRowMapper, reservationId).stream().findFirst();
    }

    public Optional<Reservation> findByIdWithPayment(UUID reservationId) {
        String sql = "SELECT * FROM reservations WHERE id = ?";
        return jdbcTemplate.query(sql, reservationRowMapper, reservationId).stream().findFirst();
    }

    public Optional<Reservation> findByIdWithTicket(UUID reservationId) {
        String sql = "SELECT * FROM reservations WHERE id = ?";
        return jdbcTemplate.query(sql, reservationRowMapper, reservationId).stream().findFirst();
    }

    public Optional<Reservation> findByIdWithUser(UUID reservationId) {
        String sql = "SELECT * FROM reservations WHERE id = ?";
        return jdbcTemplate.query(sql, reservationRowMapper, reservationId).stream().findFirst();
    }

    public List<Reservation> findExpiredPendingReservations(LocalDateTime now) {
        String sql = "SELECT * FROM reservations WHERE status = 'PENDING' AND expiry < ?";
        return jdbcTemplate.query(sql, reservationRowMapper, Timestamp.valueOf(now));
    }

    public List<Reservation> findActivePendingReservations(LocalDateTime now) {
        String sql = "SELECT * FROM reservations WHERE status = 'PENDING' AND expiry > ?";
        return jdbcTemplate.query(sql, reservationRowMapper, Timestamp.valueOf(now));
    }

    public List<Reservation> findActivePendingReservationsByUserId(UUID userId, LocalDateTime now) {
        String sql = "SELECT * FROM reservations WHERE user_id = ? AND status = 'PENDING' AND expiry > ?";
        return jdbcTemplate.query(sql, reservationRowMapper, userId, Timestamp.valueOf(now));
    }

    public List<Reservation> findPaidReservationsByUserId(UUID userId) {
        String sql = "SELECT * FROM reservations WHERE user_id = ? AND status = 'PAID'";
        return jdbcTemplate.query(sql, reservationRowMapper, userId);
    }

    public List<Reservation> findCancelledReservationsByUserId(UUID userId) {
        String sql = "SELECT * FROM reservations WHERE user_id = ? AND status = 'CANCELLED'";
        return jdbcTemplate.query(sql, reservationRowMapper, userId);
    }

    public List<Reservation> findByMatchId(UUID matchId) {
        String sql = "SELECT r.* FROM reservations r JOIN tickets t ON r.ticket_id = t.id " +
                     "JOIN ticket_categories tc ON t.ticket_category_id = tc.id WHERE tc.match_id = ?";
        return jdbcTemplate.query(sql, reservationRowMapper, matchId);
    }

    public List<Reservation> findPaidReservationsByMatchId(UUID matchId) {
        String sql = "SELECT r.* FROM reservations r JOIN tickets t ON r.ticket_id = t.id " +
                     "JOIN ticket_categories tc ON t.ticket_category_id = tc.id WHERE tc.match_id = ? AND r.status = 'PAID'";
        return jdbcTemplate.query(sql, reservationRowMapper, matchId);
    }

    public List<Reservation> findActivePendingReservationsByMatchId(UUID matchId, LocalDateTime now) {
        String sql = "SELECT r.* FROM reservations r JOIN tickets t ON r.ticket_id = t.id " +
                     "JOIN ticket_categories tc ON t.ticket_category_id = tc.id " +
                     "WHERE tc.match_id = ? AND r.status = 'PENDING' AND r.expiry > ?";
        return jdbcTemplate.query(sql, reservationRowMapper, matchId, Timestamp.valueOf(now));
    }

    public long countPaidReservationsByMatchId(UUID matchId) {
        String sql = "SELECT COUNT(r.id) FROM reservations r JOIN tickets t ON r.ticket_id = t.id " +
                     "JOIN ticket_categories tc ON t.ticket_category_id = tc.id WHERE tc.match_id = ? AND r.status = 'PAID'";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, matchId);
        return count != null ? count : 0L;
    }

    public long countPendingReservationsByMatchId(UUID matchId) {
        String sql = "SELECT COUNT(r.id) FROM reservations r JOIN tickets t ON r.ticket_id = t.id " +
                     "JOIN ticket_categories tc ON t.ticket_category_id = tc.id WHERE tc.match_id = ? AND r.status = 'PENDING'";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, matchId);
        return count != null ? count : 0L;
    }

    public long countPaidReservationsByUserId(UUID userId) {
        String sql = "SELECT COUNT(id) FROM reservations WHERE user_id = ? AND status = 'PAID'";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, userId);
        return count != null ? count : 0L;
    }

    public long countActivePendingReservationsByUserId(UUID userId, LocalDateTime now) {
        String sql = "SELECT COUNT(id) FROM reservations WHERE user_id = ? AND status = 'PENDING' AND expiry > ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, userId, Timestamp.valueOf(now));
        return count != null ? count : 0L;
    }

    public List<Object[]> countPaidReservationsByUser() {
        String sql = "SELECT user_id, COUNT(id) FROM reservations WHERE status = 'PAID' GROUP BY user_id ORDER BY COUNT(id) DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Object[]{rs.getObject("user_id"), rs.getLong(2)});
    }

    public List<Object[]> countPaidReservationsByMatch() {
        String sql = "SELECT tc.match_id, COUNT(r.id) FROM reservations r JOIN tickets t ON r.ticket_id = t.id " +
                     "JOIN ticket_categories tc ON t.ticket_category_id = tc.id WHERE r.status = 'PAID' " +
                     "GROUP BY tc.match_id ORDER BY COUNT(r.id) DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Object[]{rs.getObject("match_id"), rs.getLong(2)});
    }

    public List<Object[]> countReservationsByStatus() {
        String sql = "SELECT status, COUNT(id) FROM reservations GROUP BY status";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Object[]{rs.getString(1), rs.getLong(2)});
    }

    public List<Object[]> countReservationsByDay() {
        String sql = "SELECT CAST(reservation_date AS DATE), COUNT(id) FROM reservations GROUP BY CAST(reservation_date AS DATE) ORDER BY CAST(reservation_date AS DATE) DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Object[]{rs.getDate(1), rs.getLong(2)});
    }

    public List<Reservation> findPaidReservationsWithoutPayment() {
        String sql = "SELECT * FROM reservations WHERE payment_id IS NULL AND status = 'PAID'";
        return jdbcTemplate.query(sql, reservationRowMapper);
    }

    public List<Reservation> findPendingReservationsWithPayment() {
        String sql = "SELECT * FROM reservations WHERE payment_id IS NOT NULL AND status = 'PENDING'";
        return jdbcTemplate.query(sql, reservationRowMapper);
    }

    public Optional<Reservation> findByUserIdAndTicketId(UUID userId, UUID ticketId) {
        String sql = "SELECT * FROM reservations WHERE user_id = ? AND ticket_id = ?";
        return jdbcTemplate.query(sql, reservationRowMapper, userId, ticketId).stream().findFirst();
    }

    public Optional<Reservation> findPendingReservationByTicketId(UUID ticketId) {
        String sql = "SELECT * FROM reservations WHERE ticket_id = ? AND status = 'PENDING'";
        return jdbcTemplate.query(sql, reservationRowMapper, ticketId).stream().findFirst();
    }

    public Optional<Reservation> findPaidReservationByTicketId(UUID ticketId) {
        String sql = "SELECT * FROM reservations WHERE ticket_id = ? AND status = 'PAID'";
        return jdbcTemplate.query(sql, reservationRowMapper, ticketId).stream().findFirst();
    }

    public List<Reservation> findByUserIdAndDateAfter(UUID userId, LocalDateTime startDate) {
        String sql = "SELECT * FROM reservations WHERE user_id = ? AND reservation_date >= ?";
        return jdbcTemplate.query(sql, reservationRowMapper, userId, Timestamp.valueOf(startDate));
    }

    public List<Reservation> findByUserIdAndDateBefore(UUID userId, LocalDateTime endDate) {
        String sql = "SELECT * FROM reservations WHERE user_id = ? AND reservation_date <= ?";
        return jdbcTemplate.query(sql, reservationRowMapper, userId, Timestamp.valueOf(endDate));
    }

    public List<Reservation> findByUserIdAndDateRange(UUID userId, LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT * FROM reservations WHERE user_id = ? AND reservation_date BETWEEN ? AND ?";
        return jdbcTemplate.query(sql, reservationRowMapper, userId, Timestamp.valueOf(start), Timestamp.valueOf(end));
    }

    public List<Reservation> findPendingReservationsExpiringBetween(LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT * FROM reservations WHERE status = 'PENDING' AND expiry BETWEEN ? AND ?";
        return jdbcTemplate.query(sql, reservationRowMapper, Timestamp.valueOf(start), Timestamp.valueOf(end));
    }

    public boolean existsByTicket_IdAndStatusIn(UUID ticketId, List<ReservationStatus> statuses) {
        if (statuses == null || statuses.isEmpty()) {
            return false;
        }
        StringBuilder sql = new StringBuilder("SELECT COUNT(id) FROM reservations WHERE ticket_id = ? AND status IN (");
        for (int i = 0; i < statuses.size(); i++) {
            sql.append("?");
            if (i < statuses.size() - 1) {
                sql.append(",");
            }
        }
        sql.append(")");
        
        Object[] params = new Object[statuses.size() + 1];
        params[0] = ticketId;
        for (int i = 0; i < statuses.size(); i++) {
            params[i + 1] = statuses.get(i).name();
        }
        
        Long count = jdbcTemplate.queryForObject(sql.toString(), Long.class, params);
        return count != null && count > 0;
    }

    public boolean existsActiveReservationForTicket(UUID ticketId) {
        String sql = "SELECT COUNT(id) FROM reservations WHERE ticket_id = ? AND status IN ('PENDING', 'PAID')";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, ticketId);
        return count != null && count > 0;
    }

    public List<Reservation> findPendingReservationsWithMultipleTickets() {
        String sql = "SELECT * FROM reservations WHERE status = 'PENDING' AND quantity > 1";
        return jdbcTemplate.query(sql, reservationRowMapper);
    }
}