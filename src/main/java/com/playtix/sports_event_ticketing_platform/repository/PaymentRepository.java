package com.playtix.sports_event_ticketing_platform.repository;

import com.playtix.sports_event_ticketing_platform.domain.entity.payment.Payment;
import com.playtix.sports_event_ticketing_platform.domain.entity.payment.PaymentMethod;
import com.playtix.sports_event_ticketing_platform.domain.entity.payment.PaymentStatus;
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
public class PaymentRepository {

    private final JdbcTemplate jdbcTemplate;

    public PaymentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Payment> paymentRowMapper = (rs, rowNum) -> {
        Payment payment = new Payment();
        payment.setId(UUID.fromString(rs.getString("id")));
        
        BigDecimal amount = rs.getBigDecimal("amount");
        if (amount != null) {
            payment.setAmount(amount);
        }
        
        String methodStr = rs.getString("payment_method");
        if (methodStr != null) {
            payment.setPaymentMethod(PaymentMethod.valueOf(methodStr));
        }
        
        Timestamp paymentDate = rs.getTimestamp("payment_date");
        if (paymentDate != null) {
            payment.setPaymentDate(paymentDate.toLocalDateTime());
        }
        
        payment.setEventName(rs.getString("event_name"));
        
        String statusStr = rs.getString("status");
        if (statusStr != null) {
            payment.setStatus(PaymentStatus.valueOf(statusStr));
        }
        
        payment.setTransactionId(rs.getString("transaction_id"));
        payment.setPaymentReference(rs.getString("payment_reference"));
        payment.setBankReceiptNumber(rs.getString("bank_receipt_number"));
        payment.setFailureReason(rs.getString("failure_reason"));
        
        return payment;
    };

    // --- Standard CRUD Methods ---

    public List<Payment> findAll() {
        String sql = "SELECT * FROM payments";
        return jdbcTemplate.query(sql, paymentRowMapper);
    }

    public Optional<Payment> findById(UUID id) {
        String sql = "SELECT * FROM payments WHERE id = ?";
        List<Payment> results = jdbcTemplate.query(sql, paymentRowMapper, id);
        return results.stream().findFirst();
    }

    public Payment save(Payment payment) {
        if (payment.getId() != null && findById(payment.getId()).isPresent()) {
            String sql = "UPDATE payments SET amount = ?, payment_method = ?, payment_date = ?, event_name = ?, status = ?, transaction_id = ?, payment_reference = ?, bank_receipt_number = ?, failure_reason = ?, user_id = ? WHERE id = ?";
            jdbcTemplate.update(sql,
                    payment.getAmount(),
                    payment.getPaymentMethod() != null ? payment.getPaymentMethod().name() : null,
                    payment.getPaymentDate() != null ? Timestamp.valueOf(payment.getPaymentDate()) : null,
                    payment.getEventName(),
                    payment.getStatus() != null ? payment.getStatus().name() : null,
                    payment.getTransactionId(),
                    payment.getPaymentReference(),
                    payment.getBankReceiptNumber(),
                    payment.getFailureReason(),
                    payment.getUser() != null ? payment.getUser().getId() : null,
                    payment.getId()
            );
        } else {
            String sql = "INSERT INTO payments (id, amount, payment_method, payment_date, event_name, status, transaction_id, payment_reference, bank_receipt_number, failure_reason, user_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            if (payment.getId() == null) {
                payment.setId(UUID.randomUUID());
            }
            if (payment.getPaymentDate() == null) {
                payment.setPaymentDate(LocalDateTime.now());
            }
            if (payment.getStatus() == null) {
                payment.setStatus(PaymentStatus.PENDING);
            }
            if (payment.getTransactionId() == null) {
                payment.setTransactionId("TXN-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8));
            }
            jdbcTemplate.update(sql,
                    payment.getId(),
                    payment.getAmount(),
                    payment.getPaymentMethod() != null ? payment.getPaymentMethod().name() : null,
                    payment.getPaymentDate() != null ? Timestamp.valueOf(payment.getPaymentDate()) : null,
                    payment.getEventName(),
                    payment.getStatus().name(),
                    payment.getTransactionId(),
                    payment.getPaymentReference(),
                    payment.getBankReceiptNumber(),
                    payment.getFailureReason(),
                    payment.getUser() != null ? payment.getUser().getId() : null
            );
        }
        return payment;
    }

    public void deleteById(UUID id) {
        String sql = "DELETE FROM payments WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public void delete(Payment payment) {
        if (payment != null && payment.getId() != null) {
            deleteById(payment.getId());
        }
    }

    // --- Interface Specific Query Methods ---

    public Optional<Payment> findByTransactionId(String transactionId) {
        String sql = "SELECT * FROM payments WHERE transaction_id = ?";
        List<Payment> results = jdbcTemplate.query(sql, paymentRowMapper, transactionId);
        return results.stream().findFirst();
    }

    public Optional<Payment> findByPaymentReference(String paymentReference) {
        String sql = "SELECT * FROM payments WHERE payment_reference = ?";
        List<Payment> results = jdbcTemplate.query(sql, paymentRowMapper, paymentReference);
        return results.stream().findFirst();
    }

    public Optional<Payment> findByBankReceiptNumber(String bankReceiptNumber) {
        String sql = "SELECT * FROM payments WHERE bank_receipt_number = ?";
        List<Payment> results = jdbcTemplate.query(sql, paymentRowMapper, bankReceiptNumber);
        return results.stream().findFirst();
    }

    public List<Payment> findByUser_Id(UUID userId) {
        String sql = "SELECT * FROM payments WHERE user_id = ?";
        return jdbcTemplate.query(sql, paymentRowMapper, userId);
    }

    public List<Payment> findByStatus(PaymentStatus status) {
        String sql = "SELECT * FROM payments WHERE status = ?";
        return jdbcTemplate.query(sql, paymentRowMapper, status.name());
    }

    public List<Payment> findByPaymentMethod(PaymentMethod paymentMethod) {
        String sql = "SELECT * FROM payments WHERE payment_method = ?";
        return jdbcTemplate.query(sql, paymentRowMapper, paymentMethod.name());
    }

    public List<Payment> findByUser_IdAndStatus(UUID userId, PaymentStatus status) {
        String sql = "SELECT * FROM payments WHERE user_id = ? AND status = ?";
        return jdbcTemplate.query(sql, paymentRowMapper, userId, status.name());
    }

    public List<Payment> findByPaymentDateBetween(LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT * FROM payments WHERE payment_date BETWEEN ? AND ?";
        return jdbcTemplate.query(sql, paymentRowMapper, Timestamp.valueOf(start), Timestamp.valueOf(end));
    }

    public List<Payment> findByPaymentDateAfter(LocalDateTime date) {
        String sql = "SELECT * FROM payments WHERE payment_date > ?";
        return jdbcTemplate.query(sql, paymentRowMapper, Timestamp.valueOf(date));
    }

    public List<Payment> findByPaymentDateBefore(LocalDateTime date) {
        String sql = "SELECT * FROM payments WHERE payment_date < ?";
        return jdbcTemplate.query(sql, paymentRowMapper, Timestamp.valueOf(date));
    }

    public List<Payment> findByUser_IdOrderByPaymentDateDesc(UUID userId) {
        String sql = "SELECT * FROM payments WHERE user_id = ? ORDER BY payment_date DESC";
        return jdbcTemplate.query(sql, paymentRowMapper, userId);
    }

    public List<Payment> findByStatusOrderByPaymentDateAsc(PaymentStatus status) {
        String sql = "SELECT * FROM payments WHERE status = ? ORDER BY payment_date ASC";
        return jdbcTemplate.query(sql, paymentRowMapper, status.name());
    }

    public Optional<Payment> findByIdWithUserAndReservation(UUID paymentId) {
        String sql = "SELECT * FROM payments WHERE id = ?";
        return jdbcTemplate.query(sql, paymentRowMapper, paymentId).stream().findFirst();
    }

    public Optional<Payment> findByIdWithUser(UUID paymentId) {
        String sql = "SELECT * FROM payments WHERE id = ?";
        return jdbcTemplate.query(sql, paymentRowMapper, paymentId).stream().findFirst();
    }

    public Optional<Payment> findByIdWithReservation(UUID paymentId) {
        String sql = "SELECT * FROM payments WHERE id = ?";
        return jdbcTemplate.query(sql, paymentRowMapper, paymentId).stream().findFirst();
    }

    public Optional<Payment> findByReservationId(UUID reservationId) {
        String sql = "SELECT p.* FROM payments p JOIN reservations r ON p.id = r.payment_id WHERE r.id = ?";
        List<Payment> results = jdbcTemplate.query(sql, paymentRowMapper, reservationId);
        return results.stream().findFirst();
    }

    public List<Payment> findSuccessfulPaymentsByUserId(UUID userId) {
        String sql = "SELECT * FROM payments WHERE user_id = ? AND status = 'SUCCESSFUL'";
        return jdbcTemplate.query(sql, paymentRowMapper, userId);
    }

    public List<Payment> findPendingPaymentsByUserId(UUID userId) {
        String sql = "SELECT * FROM payments WHERE user_id = ? AND status = 'PENDING'";
        return jdbcTemplate.query(sql, paymentRowMapper, userId);
    }

    public List<Payment> findFailedPaymentsByUserId(UUID userId) {
        String sql = "SELECT * FROM payments WHERE user_id = ? AND status = 'UNSUCCESSFUL'";
        return jdbcTemplate.query(sql, paymentRowMapper, userId);
    }

    public List<Payment> findRefundedPaymentsByUserId(UUID userId) {
        String sql = "SELECT * FROM payments WHERE user_id = ? AND status = 'REFUNDED'";
        return jdbcTemplate.query(sql, paymentRowMapper, userId);
    }

    public BigDecimal sumSuccessfulAmountByUserId(UUID userId) {
        String sql = "SELECT SUM(amount) FROM payments WHERE user_id = ? AND status = 'SUCCESSFUL'";
        return jdbcTemplate.queryForObject(sql, BigDecimal.class, userId);
    }

    public BigDecimal sumSuccessfulAmountByDateRange(LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT SUM(amount) FROM payments WHERE status = 'SUCCESSFUL' AND payment_date BETWEEN ? AND ?";
        return jdbcTemplate.queryForObject(sql, BigDecimal.class, Timestamp.valueOf(start), Timestamp.valueOf(end));
    }

    public BigDecimal sumRefundedAmountByUserId(UUID userId) {
        String sql = "SELECT SUM(amount) FROM payments WHERE user_id = ? AND status = 'REFUNDED'";
        return jdbcTemplate.queryForObject(sql, BigDecimal.class, userId);
    }

    public long countSuccessfulPaymentsByDateRange(LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT COUNT(id) FROM payments WHERE status = 'SUCCESSFUL' AND payment_date BETWEEN ? AND ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, Timestamp.valueOf(start), Timestamp.valueOf(end));
        return count != null ? count : 0L;
    }

    public long countPendingPaymentsOlderThan(LocalDateTime expiryTime) {
        String sql = "SELECT COUNT(id) FROM payments WHERE status = 'PENDING' AND payment_date <= ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, Timestamp.valueOf(expiryTime));
        return count != null ? count : 0L;
    }

    public List<Object[]> countPaymentsByStatus() {
        String sql = "SELECT status, COUNT(id) FROM payments GROUP BY status";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Object[]{rs.getString(1), rs.getLong(2)});
    }

    public List<Object[]> countPaymentsByMethod() {
        String sql = "SELECT payment_method, COUNT(id) FROM payments GROUP BY payment_method";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Object[]{rs.getString(1), rs.getLong(2)});
    }

    public List<Object[]> sumSuccessfulAmountByMethod() {
        String sql = "SELECT payment_method, SUM(amount) FROM payments WHERE status = 'SUCCESSFUL' GROUP BY payment_method";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Object[]{rs.getString(1), rs.getBigDecimal(2)});
    }

    public List<Object[]> getDailyPaymentStats() {
        String sql = "SELECT CAST(payment_date AS DATE), COUNT(id), SUM(amount) FROM payments WHERE status = 'SUCCESSFUL' GROUP BY CAST(payment_date AS DATE) ORDER BY CAST(payment_date AS DATE) DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Object[]{rs.getDate(1), rs.getLong(2), rs.getBigDecimal(3)});
    }

    public List<Object[]> getMonthlyPaymentStats() {
        String sql = "SELECT EXTRACT(MONTH FROM payment_date), EXTRACT(YEAR FROM payment_date), COUNT(id), SUM(amount) FROM payments WHERE status = 'SUCCESSFUL' GROUP BY EXTRACT(MONTH FROM payment_date), EXTRACT(YEAR FROM payment_date) ORDER BY EXTRACT(YEAR FROM payment_date) DESC, EXTRACT(MONTH FROM payment_date) DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Object[]{rs.getInt(1), rs.getInt(2), rs.getLong(3), rs.getBigDecimal(4)});
    }

    public List<Payment> findExpiredPendingPayments(LocalDateTime cutoffTime) {
        String sql = "SELECT * FROM payments WHERE status = 'PENDING' AND payment_date <= ?";
        return jdbcTemplate.query(sql, paymentRowMapper, Timestamp.valueOf(cutoffTime));
    }

    public List<Payment> findByUserIdAndEventNameContaining(UUID userId, String eventName) {
        String sql = "SELECT * FROM payments WHERE user_id = ? AND event_name LIKE ?";
        return jdbcTemplate.query(sql, paymentRowMapper, userId, "%" + eventName + "%");
    }

    public List<Payment> findByTransactionIdContaining(String transactionId) {
        String sql = "SELECT * FROM payments WHERE transaction_id LIKE ?";
        return jdbcTemplate.query(sql, paymentRowMapper, "%" + transactionId + "%");
    }

    public List<Payment> findWithFailureReason() {
        String sql = "SELECT * FROM payments WHERE failure_reason IS NOT NULL";
        return jdbcTemplate.query(sql, paymentRowMapper);
    }

    public List<Payment> findWithoutFailureReason() {
        String sql = "SELECT * FROM payments WHERE failure_reason IS NULL";
        return jdbcTemplate.query(sql, paymentRowMapper);
    }

    public List<Payment> findSuccessfulPaymentsGreaterThan(BigDecimal amount) {
        String sql = "SELECT * FROM payments WHERE amount > ? AND status = 'SUCCESSFUL'";
        return jdbcTemplate.query(sql, paymentRowMapper, amount);
    }

    public List<Payment> findSuccessfulPaymentsLessThan(BigDecimal amount) {
        String sql = "SELECT * FROM payments WHERE amount < ? AND status = 'SUCCESSFUL'";
        return jdbcTemplate.query(sql, paymentRowMapper, amount);
    }

    public boolean existsByTransactionId(String transactionId) {
        String sql = "SELECT COUNT(id) FROM payments WHERE transaction_id = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, transactionId);
        return count != null && count > 0;
    }

    public boolean existsByPaymentReference(String paymentReference) {
        String sql = "SELECT COUNT(id) FROM payments WHERE payment_reference = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, paymentReference);
        return count != null && count > 0;
    }

    public boolean existsByBankReceiptNumber(String bankReceiptNumber) {
        String sql = "SELECT COUNT(id) FROM payments WHERE bank_receipt_number = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, bankReceiptNumber);
        return count != null && count > 0;
    }

    public boolean hasSuccessfulPaymentForReservation(UUID reservationId) {
        String sql = "SELECT COUNT(p.id) FROM payments p JOIN reservations r ON p.id = r.payment_id WHERE r.id = ? AND p.status = 'SUCCESSFUL'";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, reservationId);
        return count != null && count > 0;
    }

    public Optional<Payment> findPendingPaymentByReservationId(UUID reservationId) {
        String sql = "SELECT p.* FROM payments p JOIN reservations r ON p.id = r.payment_id WHERE r.id = ? AND p.status = 'PENDING'";
        List<Payment> results = jdbcTemplate.query(sql, paymentRowMapper, reservationId);
        return results.stream().findFirst();
    }
}