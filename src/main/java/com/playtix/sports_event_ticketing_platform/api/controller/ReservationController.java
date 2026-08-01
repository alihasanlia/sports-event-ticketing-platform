package com.playtix.sports_event_ticketing_platform.api.controller;

import com.playtix.sports_event_ticketing_platform.domain.dto.reservation.AdminReservationDto;
import com.playtix.sports_event_ticketing_platform.domain.dto.reservation.ConfirmReservationRequest;
import com.playtix.sports_event_ticketing_platform.domain.dto.reservation.CreateReservationRequest;
import com.playtix.sports_event_ticketing_platform.domain.dto.reservation.UserReservationDto;
import com.playtix.sports_event_ticketing_platform.domain.entity.reservation.ReservationStatus;
import com.playtix.sports_event_ticketing_platform.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<UserReservationDto> createReservation(@Valid @RequestBody CreateReservationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.createReservation(request));
    }

    @DeleteMapping("/{reservationId}/user/{userId}")
    public ResponseEntity<Void> cancelReservation(@PathVariable UUID reservationId, @PathVariable UUID userId) {
        reservationService.cancelReservation(reservationId, userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/confirm")
    public ResponseEntity<UserReservationDto> confirmReservation(@Valid @RequestBody ConfirmReservationRequest request) {
        return ResponseEntity.ok(reservationService.confirmReservation(request));
    }

    @PostMapping("/{reservationId}/expire")
    public ResponseEntity<Void> expireReservation(@PathVariable UUID reservationId) {
        reservationService.expireReservation(reservationId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{reservationId}/user")
    public ResponseEntity<UserReservationDto> getUserReservation(@PathVariable UUID reservationId) {
        return ResponseEntity.ok(reservationService.getUserReservation(reservationId));
    }

    @GetMapping("/{reservationId}/admin")
    public ResponseEntity<AdminReservationDto> getAdminReservation(@PathVariable UUID reservationId) {
        return ResponseEntity.ok(reservationService.getAdminReservation(reservationId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserReservationDto>> getUserReservations(@PathVariable UUID userId) {
        return ResponseEntity.ok(reservationService.getUserReservations(userId));
    }

    @GetMapping("/user/{userId}/active")
    public ResponseEntity<List<UserReservationDto>> getUserActiveReservations(@PathVariable UUID userId) {
        return ResponseEntity.ok(reservationService.getUserActiveReservations(userId));
    }

    @GetMapping("/user/{userId}/paid")
    public ResponseEntity<List<UserReservationDto>> getUserPaidReservations(@PathVariable UUID userId) {
        return ResponseEntity.ok(reservationService.getUserPaidReservations(userId));
    }

    @GetMapping("/admin/all")
    public ResponseEntity<List<AdminReservationDto>> getAllReservations() {
        return ResponseEntity.ok(reservationService.getAllReservations());
    }

    @GetMapping("/admin/status/{status}")
    public ResponseEntity<List<AdminReservationDto>> getReservationsByStatus(@PathVariable ReservationStatus status) {
        return ResponseEntity.ok(reservationService.getReservationsByStatus(status));
    }

    @GetMapping("/admin/pending")
    public ResponseEntity<List<AdminReservationDto>> getPendingReservations() {
        return ResponseEntity.ok(reservationService.getPendingReservations());
    }

    @GetMapping("/admin/expired")
    public ResponseEntity<List<AdminReservationDto>> getExpiredReservations() {
        return ResponseEntity.ok(reservationService.getExpiredReservations());
    }

    @PostMapping("/admin/cleanup-expired")
    public ResponseEntity<Void> cleanupExpiredReservations() {
        reservationService.cleanupExpiredReservations();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}