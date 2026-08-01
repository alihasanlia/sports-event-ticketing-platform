package com.playtix.sports_event_ticketing_platform.service;

import com.playtix.sports_event_ticketing_platform.domain.dto.payment.AdminPaymentDto;
import com.playtix.sports_event_ticketing_platform.domain.dto.payment.CreatePaymentRequest;
import com.playtix.sports_event_ticketing_platform.domain.dto.payment.ProcessPaymentRequest;
import com.playtix.sports_event_ticketing_platform.domain.dto.payment.UserPaymentDto;
import com.playtix.sports_event_ticketing_platform.domain.entity.members.User;
import com.playtix.sports_event_ticketing_platform.domain.entity.payment.Payment;
import com.playtix.sports_event_ticketing_platform.domain.entity.payment.PaymentStatus;
import com.playtix.sports_event_ticketing_platform.domain.entity.reservation.Reservation;
import com.playtix.sports_event_ticketing_platform.domain.entity.reservation.ReservationStatus;
import com.playtix.sports_event_ticketing_platform.repository.PaymentRepository;
import com.playtix.sports_event_ticketing_platform.repository.ReservationRepository;
import com.playtix.sports_event_ticketing_platform.repository.UserRepository;
import com.playtix.sports_event_ticketing_platform.mapper.PaymentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final PaymentMapper paymentMapper;

    @Transactional
    public UserPaymentDto createPayment(CreatePaymentRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Reservation reservation = reservationRepository.findByIdWithTicket(request.reservationId())
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        if (!reservation.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Reservation does not belong to this user");
        }

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new RuntimeException("Only pending reservations can be paid");
        }

        if (reservation.isExpiredNow()) {
            throw new RuntimeException("Reservation has expired");
        }

        if (paymentRepository.findPendingPaymentByReservationId(reservation.getId()).isPresent()) {
            throw new RuntimeException("A pending payment already exists for this reservation");
        }

        Payment payment = new Payment();
        payment.setAmount(request.amount());
        payment.setPaymentMethod(request.paymentMethod());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setUser(user);
        payment.setEventName(reservation.getMatchDescription());

        payment = paymentRepository.save(payment);
        reservation.associatePayment(payment);
        reservationRepository.save(reservation);

        return paymentMapper.toUserPaymentDto(payment);
    }

    @Transactional
    public UserPaymentDto processPayment(ProcessPaymentRequest request) {
        Payment payment = paymentRepository.findByIdWithReservation(request.paymentId())
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new RuntimeException("Payment is not pending");
        }

        try {
            payment.setBankReceiptNumber(request.bankReceiptNumber());
            payment.markAsCompleted();
            payment = paymentRepository.save(payment);

            Reservation reservation = payment.getReservation();
            if (reservation != null) {
                reservation.confirm();
                reservationRepository.save(reservation);
            }

            return paymentMapper.toUserPaymentDto(payment);

        } catch (Exception e) {
            payment.markAsFailed(e.getMessage());
            payment = paymentRepository.save(payment);
            throw new RuntimeException("Payment processing failed: " + e.getMessage());
        }
    }

    @Transactional
    public UserPaymentDto refundPayment(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (payment.getStatus() != PaymentStatus.SUCCESSFUL) {
            throw new RuntimeException("Only successful payments can be refunded");
        }

        payment.markAsRefunded();
        payment = paymentRepository.save(payment);

        return paymentMapper.toUserPaymentDto(payment);
    }

    @Transactional(readOnly = true)
    public UserPaymentDto getUserPayment(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        return paymentMapper.toUserPaymentDto(payment);
    }

    @Transactional(readOnly = true)
    public AdminPaymentDto getAdminPayment(UUID paymentId) {
        Payment payment = paymentRepository.findByIdWithUser(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        return paymentMapper.toAdminPaymentDto(payment);
    }

    @Transactional(readOnly = true)
    public List<UserPaymentDto> getUserPayments(UUID userId) {
        List<Payment> payments = paymentRepository.findByUser_IdOrderByPaymentDateDesc(userId);
        return paymentMapper.toUserPaymentDtoList(payments);
    }

    @Transactional(readOnly = true)
    public List<UserPaymentDto> getUserSuccessfulPayments(UUID userId) {
        List<Payment> payments = paymentRepository.findSuccessfulPaymentsByUserId(userId);
        return paymentMapper.toUserPaymentDtoList(payments);
    }

    @Transactional(readOnly = true)
    public List<AdminPaymentDto> getAllPayments() {
        List<Payment> payments = paymentRepository.findAll();
        return paymentMapper.toAdminPaymentDtoList(payments);
    }

    @Transactional(readOnly = true)
    public List<AdminPaymentDto> getPaymentsByStatus(PaymentStatus status) {
        List<Payment> payments = paymentRepository.findByStatus(status);
        return paymentMapper.toAdminPaymentDtoList(payments);
    }

    @Transactional(readOnly = true)
    public List<AdminPaymentDto> getPendingPayments() {
        List<Payment> payments = paymentRepository.findByStatus(PaymentStatus.PENDING);
        return paymentMapper.toAdminPaymentDtoList(payments);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalAmountByUser(UUID userId) {
        BigDecimal total = paymentRepository.sumSuccessfulAmountByUserId(userId);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalAmountByDateRange(LocalDateTime start, LocalDateTime end) {
        BigDecimal total = paymentRepository.sumSuccessfulAmountByDateRange(start, end);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional
    public void cleanupExpiredPendingPayments() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(30);
        List<Payment> expiredPayments = paymentRepository.findExpiredPendingPayments(cutoffTime);
        
        for (Payment payment : expiredPayments) {
            payment.markAsFailed("Payment expired");
            paymentRepository.save(payment);
        }
    }
}