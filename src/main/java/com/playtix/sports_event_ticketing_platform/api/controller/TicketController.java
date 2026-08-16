package com.playtix.sports_event_ticketing_platform.api.controller;

import com.playtix.sports_event_ticketing_platform.domain.dto.ticket.CreateTicketRequest;
import com.playtix.sports_event_ticketing_platform.domain.dto.ticket.TicketDetailsDto;
import com.playtix.sports_event_ticketing_platform.domain.dto.ticket.TicketSummaryDto;
import com.playtix.sports_event_ticketing_platform.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    public ResponseEntity<TicketSummaryDto> createTicket(@Valid @RequestBody CreateTicketRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ticketService.createTicket(request));
    }

    @GetMapping
    public ResponseEntity<List<TicketSummaryDto>> getAllTicketsSummary() {
        return ResponseEntity.ok(ticketService.getTicketsSummary());
    }
    
    @GetMapping("/{ticketId}/summary")
    public ResponseEntity<TicketSummaryDto> getTicketSummary(@PathVariable UUID ticketId) {
        return ResponseEntity.ok(ticketService.getTicketSummary(ticketId));
    }

    @GetMapping("/{ticketId}/details")
    public ResponseEntity<TicketDetailsDto> getTicketDetails(@PathVariable UUID ticketId) {
        return ResponseEntity.ok(ticketService.getTicketDetails(ticketId));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<TicketSummaryDto>> getTicketsByCategory(@PathVariable UUID categoryId) {
        return ResponseEntity.ok(ticketService.getTicketsByCategory(categoryId));
    }

    @GetMapping("/category/{categoryId}/available")
    public ResponseEntity<List<TicketSummaryDto>> getAvailableTicketsByCategory(@PathVariable UUID categoryId) {
        return ResponseEntity.ok(ticketService.getAvailableTicketsByCategory(categoryId));
    }

    @GetMapping("/match/{matchId}")
    public ResponseEntity<List<TicketSummaryDto>> getTicketsByMatch(@PathVariable UUID matchId) {
        return ResponseEntity.ok(ticketService.getTicketsByMatch(matchId));
    }

    @GetMapping("/match/{matchId}/available")
    public ResponseEntity<List<TicketSummaryDto>> getAvailableTicketsByMatch(@PathVariable UUID matchId) {
        return ResponseEntity.ok(ticketService.getAvailableTicketsByMatch(matchId));
    }

    @GetMapping("/barcode/{barcode}")
    public ResponseEntity<TicketSummaryDto> getTicketByBarcode(@PathVariable String barcode) {
        return ResponseEntity.ok(ticketService.getTicketByBarcode(barcode));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TicketSummaryDto>> getTicketsByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(ticketService.getTicketsByUserId(userId));
    }

    @GetMapping("/user/{userId}/purchased")
    public ResponseEntity<List<TicketSummaryDto>> getPurchasedTicketsByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(ticketService.getPurchasedTicketsByUserId(userId));
    }

    @GetMapping("/user/{userId}/reserved")
    public ResponseEntity<List<TicketSummaryDto>> getReservedTicketsByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(ticketService.getReservedTicketsByUserId(userId));
    }
}