package com.playtix.sports_event_ticketing_platform.repository;

import com.playtix.sports_event_ticketing_platform.domain.entity.TicketCancellation;
import com.playtix.sports_event_ticketing_platform.domain.entity.members.User;
import com.playtix.sports_event_ticketing_platform.domain.entity.ticket.Ticket;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TicketCancellationRepository {

    private final JdbcTemplate jdbcTemplate;

    public TicketCancellationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<TicketCancellation> rowMapper = (rs, rowNum) -> {
        TicketCancellation tc = new TicketCancellation();
        tc.setId(rs.getObject("id", UUID.class));
        tc.setPenaltyPercent(rs.getInt("penalty_percent"));
        tc.setRefundAmount(rs.getBigDecimal("refund_amount"));
        tc.setCancellationFee(rs.getBigDecimal("cancellation_fee"));
        Timestamp requestDateTs = rs.getTimestamp("request_date");
        if (requestDateTs != null) {
            tc.setRequestDate(requestDateTs.toLocalDateTime());
        }
        tc.setReason(rs.getString("reason"));

        UUID userId = rs.getObject("user_id", UUID.class);
        if (userId != null) {
            User user = new User();
            user.setId(userId);
            tc.setUser(user);
        }

        UUID ticketId = rs.getObject("ticket_id", UUID.class);
        if (ticketId != null) {
            Ticket ticket = new Ticket();
            ticket.setId(ticketId);
            tc.setTicket(ticket);
        }

        return tc;
    };

    public Optional<TicketCancellation> findById(UUID id) {
        String sql = "SELECT * FROM ticket_cancellations WHERE id = ?";
        List<TicketCancellation> results = jdbcTemplate.query(sql, rowMapper, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public List<TicketCancellation> findAll() {
        String sql = "SELECT * FROM ticket_cancellations";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public TicketCancellation save(TicketCancellation ticketCancellation) {
        if (ticketCancellation.getId() == null) {
            ticketCancellation.setId(UUID.randomUUID());
        }
        if (ticketCancellation.getRequestDate() == null) {
            ticketCancellation.setRequestDate(LocalDateTime.now());
        }

        String checkSql = "SELECT COUNT(*) FROM ticket_cancellations WHERE id = ?";
        Long count = jdbcTemplate.queryForObject(checkSql, Long.class, ticketCancellation.getId());

        if (count == null || count == 0) {
            String insertSql = "INSERT INTO ticket_cancellations (id, penalty_percent, refund_amount, cancellation_fee, request_date, reason, user_id, ticket_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(insertSql,
                    ticketCancellation.getId(),
                    ticketCancellation.getPenaltyPercent(),
                    ticketCancellation.getRefundAmount(),
                    ticketCancellation.getCancellationFee(),
                    ticketCancellation.getRequestDate() != null ? Timestamp.valueOf(ticketCancellation.getRequestDate()) : null,
                    ticketCancellation.getReason(),
                    ticketCancellation.getUser() != null ? ticketCancellation.getUser().getId() : null,
                    ticketCancellation.getTicket() != null ? ticketCancellation.getTicket().getId() : null
            );
        } else {
            String updateSql = "UPDATE ticket_cancellations SET penalty_percent = ?, refund_amount = ?, cancellation_fee = ?, request_date = ?, reason = ?, user_id = ?, ticket_id = ? WHERE id = ?";
            jdbcTemplate.update(updateSql,
                    ticketCancellation.getPenaltyPercent(),
                    ticketCancellation.getRefundAmount(),
                    ticketCancellation.getCancellationFee(),
                    ticketCancellation.getRequestDate() != null ? Timestamp.valueOf(ticketCancellation.getRequestDate()) : null,
                    ticketCancellation.getReason(),
                    ticketCancellation.getUser() != null ? ticketCancellation.getUser().getId() : null,
                    ticketCancellation.getTicket() != null ? ticketCancellation.getTicket().getId() : null,
                    ticketCancellation.getId()
            );
        }
        return ticketCancellation;
    }

    public void deleteById(UUID id) {
        String sql = "DELETE FROM ticket_cancellations WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public void delete(TicketCancellation ticketCancellation) {
        if (ticketCancellation != null && ticketCancellation.getId() != null) {
            deleteById(ticketCancellation.getId());
        }
    }

    public List<TicketCancellation> findByUser_Id(UUID userId) {
        String sql = "SELECT * FROM ticket_cancellations WHERE user_id = ?";
        return jdbcTemplate.query(sql, rowMapper, userId);
    }

    public List<TicketCancellation> findByTicket_Id(UUID ticketId) {
        String sql = "SELECT * FROM ticket_cancellations WHERE ticket_id = ?";
        return jdbcTemplate.query(sql, rowMapper, ticketId);
    }

    public List<TicketCancellation> findByUser_IdOrderByRequestDateDesc(UUID userId) {
        String sql = "SELECT * FROM ticket_cancellations WHERE user_id = ? ORDER BY request_date DESC";
        return jdbcTemplate.query(sql, rowMapper, userId);
    }

    public List<TicketCancellation> findByRequestDateBetween(LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT * FROM ticket_cancellations WHERE request_date BETWEEN ? AND ?";
        return jdbcTemplate.query(sql, rowMapper, Timestamp.valueOf(start), Timestamp.valueOf(end));
    }

    public List<TicketCancellation> findByRequestDateAfter(LocalDateTime date) {
        String sql = "SELECT * FROM ticket_cancellations WHERE request_date > ?";
        return jdbcTemplate.query(sql, rowMapper, Timestamp.valueOf(date));
    }

    public List<TicketCancellation> findByRequestDateBefore(LocalDateTime date) {
        String sql = "SELECT * FROM ticket_cancellations WHERE request_date < ?";
        return jdbcTemplate.query(sql, rowMapper, Timestamp.valueOf(date));
    }

    public List<TicketCancellation> findByPenaltyPercent(int penaltyPercent) {
        String sql = "SELECT * FROM ticket_cancellations WHERE penalty_percent = ?";
        return jdbcTemplate.query(sql, rowMapper, penaltyPercent);
    }

    public List<TicketCancellation> findByPenaltyPercentBetween(int minPenalty, int maxPenalty) {
        String sql = "SELECT * FROM ticket_cancellations WHERE penalty_percent BETWEEN ? AND ?";
        return jdbcTemplate.query(sql, rowMapper, minPenalty, maxPenalty);
    }

    public List<TicketCancellation> findByRefundAmountGreaterThan(BigDecimal amount) {
        String sql = "SELECT * FROM ticket_cancellations WHERE refund_amount > ?";
        return jdbcTemplate.query(sql, rowMapper, amount);
    }

    public List<TicketCancellation> findByRefundAmountLessThan(BigDecimal amount) {
        String sql = "SELECT * FROM ticket_cancellations WHERE refund_amount < ?";
        return jdbcTemplate.query(sql, rowMapper, amount);
    }

    public List<TicketCancellation> findByIdWithUserAndTicket(UUID cancellationId) {
        String sql = "SELECT * FROM ticket_cancellations WHERE id = ?";
        return jdbcTemplate.query(sql, rowMapper, cancellationId);
    }

    public List<TicketCancellation> findByIdWithUser(UUID cancellationId) {
        String sql = "SELECT * FROM ticket_cancellations WHERE id = ?";
        return jdbcTemplate.query(sql, rowMapper, cancellationId);
    }

    public List<TicketCancellation> findByIdWithTicket(UUID cancellationId) {
        String sql = "SELECT * FROM ticket_cancellations WHERE id = ?";
        return jdbcTemplate.query(sql, rowMapper, cancellationId);
    }

    public List<TicketCancellation> findByUserIdAndDateAfter(UUID userId, LocalDateTime startDate) {
        String sql = "SELECT * FROM ticket_cancellations WHERE user_id = ? AND request_date >= ?";
        return jdbcTemplate.query(sql, rowMapper, userId, Timestamp.valueOf(startDate));
    }

    public List<TicketCancellation> findByUserIdAndDateBefore(UUID userId, LocalDateTime endDate) {
        String sql = "SELECT * FROM ticket_cancellations WHERE user_id = ? AND request_date <= ?";
        return jdbcTemplate.query(sql, rowMapper, userId, Timestamp.valueOf(endDate));
    }

    public List<TicketCancellation> findByMatchId(UUID matchId) {
        String sql = "SELECT tc.* FROM ticket_cancellations tc JOIN tickets t ON tc.ticket_id = t.id WHERE t.match_id = ?";
        return jdbcTemplate.query(sql, rowMapper, matchId);
    }

    public List<TicketCancellation> findByMatchIdAndDateRange(UUID matchId, LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT tc.* FROM ticket_cancellations tc JOIN tickets t ON tc.ticket_id = t.id WHERE t.match_id = ? AND tc.request_date BETWEEN ? AND ?";
        return jdbcTemplate.query(sql, rowMapper, matchId, Timestamp.valueOf(start), Timestamp.valueOf(end));
    }

    public BigDecimal sumRefundAmountByUserId(UUID userId) {
        String sql = "SELECT SUM(refund_amount) FROM ticket_cancellations WHERE user_id = ?";
        return jdbcTemplate.queryForObject(sql, BigDecimal.class, userId);
    }

    public BigDecimal sumRefundAmountByMatchId(UUID matchId) {
        String sql = "SELECT SUM(tc.refund_amount) FROM ticket_cancellations tc JOIN tickets t ON tc.ticket_id = t.id WHERE t.match_id = ?";
        return jdbcTemplate.queryForObject(sql, BigDecimal.class, matchId);
    }

    public BigDecimal sumRefundAmountByDateRange(LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT SUM(refund_amount) FROM ticket_cancellations WHERE request_date BETWEEN ? AND ?";
        return jdbcTemplate.queryForObject(sql, BigDecimal.class, Timestamp.valueOf(start), Timestamp.valueOf(end));
    }

    public Double averagePenaltyPercent() {
        String sql = "SELECT AVG(CAST(penalty_percent AS DOUBLE)) FROM ticket_cancellations";
        return jdbcTemplate.queryForObject(sql, Double.class);
    }

    public Double averagePenaltyPercentByUserId(UUID userId) {
        String sql = "SELECT AVG(CAST(penalty_percent AS DOUBLE)) FROM ticket_cancellations WHERE user_id = ?";
        return jdbcTemplate.queryForObject(sql, Double.class, userId);
    }

    public long countByUserId(UUID userId) {
        String sql = "SELECT COUNT(*) FROM ticket_cancellations WHERE user_id = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, userId);
        return count != null ? count : 0;
    }

    public long countByMatchId(UUID matchId) {
        String sql = "SELECT COUNT(tc.id) FROM ticket_cancellations tc JOIN tickets t ON tc.ticket_id = t.id WHERE t.match_id = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, matchId);
        return count != null ? count : 0;
    }

    public List<Object[]> countCancellationsByUser() {
        String sql = "SELECT user_id, COUNT(*) FROM ticket_cancellations GROUP BY user_id ORDER BY COUNT(*) DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Object[]{rs.getObject(1, UUID.class), rs.getLong(2)});
    }

    public List<Object[]> countCancellationsByMatch() {
        String sql = "SELECT t.match_id, COUNT(tc.id) FROM ticket_cancellations tc JOIN tickets t ON tc.ticket_id = t.id GROUP BY t.match_id ORDER BY COUNT(tc.id) DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Object[]{rs.getObject(1, UUID.class), rs.getLong(2)});
    }

    public List<Object[]> countCancellationsByTicket() {
        String sql = "SELECT ticket_id, COUNT(*) FROM ticket_cancellations GROUP BY ticket_id ORDER BY COUNT(*) DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Object[]{rs.getObject(1, UUID.class), rs.getLong(2)});
    }

    public List<Object[]> countCancellationsByDay() {
        String sql = "SELECT CAST(request_date AS DATE), COUNT(*) FROM ticket_cancellations GROUP BY CAST(request_date AS DATE) ORDER BY CAST(request_date AS DATE) DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Object[]{rs.getObject(1), rs.getLong(2)});
    }

    public List<Object[]> countCancellationsByMonth() {
        String sql = "SELECT EXTRACT(MONTH FROM request_date), EXTRACT(YEAR FROM request_date), COUNT(*) FROM ticket_cancellations GROUP BY EXTRACT(MONTH FROM request_date), EXTRACT(YEAR FROM request_date) ORDER BY EXTRACT(YEAR FROM request_date) DESC, EXTRACT(MONTH FROM request_date) DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Object[]{rs.getInt(1), rs.getInt(2), rs.getLong(3)});
    }

    public List<TicketCancellation> findByUserIdAndRefundAmountGreaterThan(UUID userId, BigDecimal amount) {
        String sql = "SELECT * FROM ticket_cancellations WHERE user_id = ? AND refund_amount > ?";
        return jdbcTemplate.query(sql, rowMapper, userId, amount);
    }

    public List<TicketCancellation> findByTicketIdOrderByRequestDateDesc(UUID ticketId) {
        String sql = "SELECT * FROM ticket_cancellations WHERE ticket_id = ? ORDER BY request_date DESC";
        return jdbcTemplate.query(sql, rowMapper, ticketId);
    }

    public List<TicketCancellation> findByUserIdAndTicketId(UUID userId, UUID ticketId) {
        String sql = "SELECT * FROM ticket_cancellations WHERE user_id = ? AND ticket_id = ?";
        return jdbcTemplate.query(sql, rowMapper, userId, ticketId);
    }

    public List<TicketCancellation> findWithCancellationFee() {
        String sql = "SELECT * FROM ticket_cancellations WHERE cancellation_fee IS NOT NULL";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public List<TicketCancellation> findWithoutCancellationFee() {
        String sql = "SELECT * FROM ticket_cancellations WHERE cancellation_fee IS NULL";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public List<TicketCancellation> findFullRefundCancellations() {
        String sql = "SELECT * FROM ticket_cancellations WHERE penalty_percent = 0";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public List<TicketCancellation> findPartialRefundCancellations() {
        String sql = "SELECT * FROM ticket_cancellations WHERE penalty_percent > 0";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public boolean existsByTicket_Id(UUID ticketId) {
        String sql = "SELECT COUNT(*) FROM ticket_cancellations WHERE ticket_id = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, ticketId);
        return count != null && count > 0;
    }

    public boolean hasCancellationForTicket(UUID ticketId) {
        String sql = "SELECT COUNT(*) FROM ticket_cancellations WHERE ticket_id = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, ticketId);
        return count != null && count > 0;
    }
}