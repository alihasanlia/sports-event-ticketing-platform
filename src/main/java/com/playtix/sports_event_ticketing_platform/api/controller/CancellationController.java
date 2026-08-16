package com.playtix.sports_event_ticketing_platform.api.controller;

import com.playtix.sports_event_ticketing_platform.domain.dto.cancellation.CancellationDto;
import com.playtix.sports_event_ticketing_platform.domain.dto.cancellation.CreateCancellationRequestDto;
import com.playtix.sports_event_ticketing_platform.service.cancellation.CancellationEligibility;
import com.playtix.sports_event_ticketing_platform.service.cancellation.CancellationService;
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
@RequestMapping("/api/v1/cancellations")
@RequiredArgsConstructor
public class CancellationController {

    private final CancellationService cancellationService;

    @PostMapping
    public ResponseEntity<CancellationDto> createCancellation(@Valid @RequestBody CreateCancellationRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cancellationService.createCancellation(request));
    }

    @GetMapping("/{cancellationId}")
    public ResponseEntity<CancellationDto> getCancellationById(@PathVariable UUID cancellationId) {
        return ResponseEntity.ok(cancellationService.getCancellationById(cancellationId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CancellationDto>> getUserCancellations(@PathVariable UUID userId) {
        return ResponseEntity.ok(cancellationService.getUserCancellations(userId));
    }

    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<List<CancellationDto>> getTicketCancellations(@PathVariable UUID ticketId) {
        return ResponseEntity.ok(cancellationService.getTicketCancellations(ticketId));
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<CancellationDto>> getCancellationsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(cancellationService.getCancellationsByDateRange(start, end));
    }

    @GetMapping("/user/{userId}/total-refund")
    public ResponseEntity<BigDecimal> getTotalRefundAmountByUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(cancellationService.getTotalRefundAmountByUser(userId));
    }

    @GetMapping("/match/{matchId}/total-refund")
    public ResponseEntity<BigDecimal> getTotalRefundAmountByMatch(@PathVariable UUID matchId) {
        return ResponseEntity.ok(cancellationService.getTotalRefundAmountByMatch(matchId));
    }

    @GetMapping("/ticket/{ticketId}/penalty")
    public ResponseEntity<Integer> calculatePenaltyPercent(@PathVariable UUID ticketId) {
        return ResponseEntity.ok(cancellationService.calculatePenaltyPercentForTicket(ticketId));
    }

    @GetMapping("/ticket/{ticketId}/refund-amount")
    public ResponseEntity<BigDecimal> calculateRefundAmount(@PathVariable UUID ticketId) {
        return ResponseEntity.ok(cancellationService.calculateRefundAmountForTicket(ticketId));
    }

    @GetMapping("/ticket/{ticketId}/eligibility")
    public ResponseEntity<CancellationEligibility> checkCancellationEligibility(@PathVariable UUID ticketId) {
        return ResponseEntity.ok(cancellationService.checkCancellationEligibility(ticketId));
    }
}