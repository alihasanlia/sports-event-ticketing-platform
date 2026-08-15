package com.playtix.sports_event_ticketing_platform.api.controller;

import com.playtix.sports_event_ticketing_platform.domain.dto.payment.AdminPaymentDto;
import com.playtix.sports_event_ticketing_platform.domain.dto.payment.CreatePaymentRequest;
import com.playtix.sports_event_ticketing_platform.domain.dto.payment.ProcessPaymentRequest;
import com.playtix.sports_event_ticketing_platform.domain.dto.payment.UserPaymentDto;
import com.playtix.sports_event_ticketing_platform.domain.entity.payment.PaymentStatus;
import com.playtix.sports_event_ticketing_platform.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<UserPaymentDto> createPayment(@Valid @RequestBody CreatePaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.createPayment(request));
    }

    @PostMapping("/process")
    public ResponseEntity<UserPaymentDto> processPayment(@Valid @RequestBody ProcessPaymentRequest request) {
        return ResponseEntity.ok(paymentService.processPayment(request));
    }

    @GetMapping("/{paymentId}/user")
    public ResponseEntity<UserPaymentDto> getUserPayment(@PathVariable UUID paymentId) {
        return ResponseEntity.ok(paymentService.getUserPayment(paymentId));
    }

    @GetMapping("/{paymentId}/admin")
    public ResponseEntity<AdminPaymentDto> getAdminPayment(@PathVariable UUID paymentId) {
        return ResponseEntity.ok(paymentService.getAdminPayment(paymentId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserPaymentDto>> getUserPayments(@PathVariable UUID userId) {
        return ResponseEntity.ok(paymentService.getUserPayments(userId));
    }

    @GetMapping("/user/{userId}/successful")
    public ResponseEntity<List<UserPaymentDto>> getUserSuccessfulPayments(@PathVariable UUID userId) {
        return ResponseEntity.ok(paymentService.getUserSuccessfulPayments(userId));
    }

    @GetMapping("/admin/all")
    public ResponseEntity<List<AdminPaymentDto>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @GetMapping("/admin/status/{status}")
    public ResponseEntity<List<AdminPaymentDto>> getPaymentsByStatus(@PathVariable PaymentStatus status) {
        return ResponseEntity.ok(paymentService.getPaymentsByStatus(status));
    }

    @GetMapping("/admin/pending")
    public ResponseEntity<List<AdminPaymentDto>> getPendingPayments() {
        return ResponseEntity.ok(paymentService.getPendingPayments());
    }

    @GetMapping("/user/{userId}/total-amount")
    public ResponseEntity<BigDecimal> getTotalAmountByUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(paymentService.getTotalAmountByUser(userId));
    }

    @GetMapping("/admin/total-amount")
    public ResponseEntity<BigDecimal> getTotalAmountByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(paymentService.getTotalAmountByDateRange(start, end));
    }

    @PostMapping("/admin/cleanup-expired")
    public ResponseEntity<Void> cleanupExpiredPendingPayments() {
        paymentService.cleanupExpiredPendingPayments();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}