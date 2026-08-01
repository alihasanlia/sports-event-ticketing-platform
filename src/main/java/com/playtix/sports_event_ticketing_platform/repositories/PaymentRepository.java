package com.playtix.sports_event_ticketing_platform.repositories;

import com.playtix.sports_event_ticketing_platform.domain.entity.payment.Payment;
import com.playtix.sports_event_ticketing_platform.domain.entity.payment.PaymentMethod;
import com.playtix.sports_event_ticketing_platform.domain.entity.payment.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Optional<Payment> findByTransactionId(String transactionId);

    Optional<Payment> findByPaymentReference(String paymentReference);

    Optional<Payment> findByBankReceiptNumber(String bankReceiptNumber);

    List<Payment> findByUser_Id(UUID userId);

    List<Payment> findByStatus(PaymentStatus status);

    List<Payment> findByPaymentMethod(PaymentMethod paymentMethod);

    List<Payment> findByUser_IdAndStatus(UUID userId, PaymentStatus status);

    List<Payment> findByPaymentDateBetween(LocalDateTime start, LocalDateTime end);

    List<Payment> findByPaymentDateAfter(LocalDateTime date);

    List<Payment> findByPaymentDateBefore(LocalDateTime date);

    List<Payment> findByUser_IdOrderByPaymentDateDesc(UUID userId);

    List<Payment> findByStatusOrderByPaymentDateAsc(PaymentStatus status);

    @Query("SELECT p FROM Payment p LEFT JOIN FETCH p.user LEFT JOIN FETCH p.reservation WHERE p.id = :paymentId")
    Optional<Payment> findByIdWithUserAndReservation(@Param("paymentId") UUID paymentId);

    @Query("SELECT p FROM Payment p LEFT JOIN FETCH p.user WHERE p.id = :paymentId")
    Optional<Payment> findByIdWithUser(@Param("paymentId") UUID paymentId);

    @Query("SELECT p FROM Payment p LEFT JOIN FETCH p.reservation WHERE p.id = :paymentId")
    Optional<Payment> findByIdWithReservation(@Param("paymentId") UUID paymentId);

    @Query("SELECT p FROM Payment p WHERE p.reservation.id = :reservationId")
    Optional<Payment> findByReservationId(@Param("reservationId") UUID reservationId);

    @Query("SELECT p FROM Payment p WHERE p.user.id = :userId AND p.status = 'SUCCESSFUL'")
    List<Payment> findSuccessfulPaymentsByUserId(@Param("userId") UUID userId);

    @Query("SELECT p FROM Payment p WHERE p.user.id = :userId AND p.status = 'PENDING'")
    List<Payment> findPendingPaymentsByUserId(@Param("userId") UUID userId);

    @Query("SELECT p FROM Payment p WHERE p.user.id = :userId AND p.status = 'UNSUCCESSFUL'")
    List<Payment> findFailedPaymentsByUserId(@Param("userId") UUID userId);

    @Query("SELECT p FROM Payment p WHERE p.user.id = :userId AND p.status = 'REFUNDED'")
    List<Payment> findRefundedPaymentsByUserId(@Param("userId") UUID userId);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.user.id = :userId AND p.status = 'SUCCESSFUL'")
    BigDecimal sumSuccessfulAmountByUserId(@Param("userId") UUID userId);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'SUCCESSFUL' AND p.paymentDate BETWEEN :start AND :end")
    BigDecimal sumSuccessfulAmountByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.user.id = :userId AND p.status = 'REFUNDED'")
    BigDecimal sumRefundedAmountByUserId(@Param("userId") UUID userId);

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = 'SUCCESSFUL' AND p.paymentDate BETWEEN :start AND :end")
    long countSuccessfulPaymentsByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = 'PENDING' AND p.paymentDate <= :expiryTime")
    long countPendingPaymentsOlderThan(@Param("expiryTime") LocalDateTime expiryTime);

    @Query("SELECT p.status, COUNT(p) FROM Payment p GROUP BY p.status")
    List<Object[]> countPaymentsByStatus();

    @Query("SELECT p.paymentMethod, COUNT(p) FROM Payment p GROUP BY p.paymentMethod")
    List<Object[]> countPaymentsByMethod();

    @Query("SELECT p.paymentMethod, SUM(p.amount) FROM Payment p WHERE p.status = 'SUCCESSFUL' GROUP BY p.paymentMethod")
    List<Object[]> sumSuccessfulAmountByMethod();

    @Query("SELECT FUNCTION('DATE', p.paymentDate), COUNT(p), SUM(p.amount) FROM Payment p WHERE p.status = 'SUCCESSFUL' GROUP BY FUNCTION('DATE', p.paymentDate) ORDER BY FUNCTION('DATE', p.paymentDate) DESC")
    List<Object[]> getDailyPaymentStats();

    @Query("SELECT FUNCTION('MONTH', p.paymentDate), FUNCTION('YEAR', p.paymentDate), COUNT(p), SUM(p.amount) FROM Payment p WHERE p.status = 'SUCCESSFUL' GROUP BY FUNCTION('MONTH', p.paymentDate), FUNCTION('YEAR', p.paymentDate) ORDER BY FUNCTION('YEAR', p.paymentDate) DESC, FUNCTION('MONTH', p.paymentDate) DESC")
    List<Object[]> getMonthlyPaymentStats();

    @Query("SELECT p FROM Payment p WHERE p.status = 'PENDING' AND p.paymentDate <= :cutoffTime")
    List<Payment> findExpiredPendingPayments(@Param("cutoffTime") LocalDateTime cutoffTime);

    @Query("SELECT p FROM Payment p WHERE p.user.id = :userId AND p.eventName LIKE %:eventName%")
    List<Payment> findByUserIdAndEventNameContaining(@Param("userId") UUID userId, @Param("eventName") String eventName);

    @Query("SELECT p FROM Payment p WHERE p.transactionId LIKE %:transactionId%")
    List<Payment> findByTransactionIdContaining(@Param("transactionId") String transactionId);

    @Query("SELECT p FROM Payment p WHERE p.failureReason IS NOT NULL")
    List<Payment> findWithFailureReason();

    @Query("SELECT p FROM Payment p WHERE p.failureReason IS NULL")
    List<Payment> findWithoutFailureReason();

    @Query("SELECT p FROM Payment p WHERE p.amount > :amount AND p.status = 'SUCCESSFUL'")
    List<Payment> findSuccessfulPaymentsGreaterThan(@Param("amount") BigDecimal amount);

    @Query("SELECT p FROM Payment p WHERE p.amount < :amount AND p.status = 'SUCCESSFUL'")
    List<Payment> findSuccessfulPaymentsLessThan(@Param("amount") BigDecimal amount);

    boolean existsByTransactionId(String transactionId);

    boolean existsByPaymentReference(String paymentReference);

    boolean existsByBankReceiptNumber(String bankReceiptNumber);

    @Query("SELECT COUNT(p) > 0 FROM Payment p WHERE p.reservation.id = :reservationId AND p.status = 'SUCCESSFUL'")
    boolean hasSuccessfulPaymentForReservation(@Param("reservationId") UUID reservationId);

    @Query("SELECT p FROM Payment p WHERE p.reservation.id = :reservationId AND p.status = 'PENDING'")
    Optional<Payment> findPendingPaymentByReservationId(@Param("reservationId") UUID reservationId);
}
