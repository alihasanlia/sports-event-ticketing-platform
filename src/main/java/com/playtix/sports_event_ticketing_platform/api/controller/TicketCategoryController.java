package com.playtix.sports_event_ticketing_platform.api.controller;

import com.playtix.sports_event_ticketing_platform.domain.dto.ticket.CreateTicketCategoryRequest;
import com.playtix.sports_event_ticketing_platform.domain.dto.ticket.TicketCategoryDto;
import com.playtix.sports_event_ticketing_platform.domain.dto.ticket.UpdateTicketCategoryRequest;
import com.playtix.sports_event_ticketing_platform.domain.entity.ticket.Category;
import com.playtix.sports_event_ticketing_platform.service.TicketCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ticket-categories")
@RequiredArgsConstructor
public class TicketCategoryController {

    private final TicketCategoryService ticketCategoryService;

    @PostMapping
    public ResponseEntity<TicketCategoryDto> createTicketCategory(@Valid @RequestBody CreateTicketCategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ticketCategoryService.createTicketCategory(request));
    }

    @PutMapping
    public ResponseEntity<TicketCategoryDto> updateTicketCategory(@Valid @RequestBody UpdateTicketCategoryRequest request) {
        return ResponseEntity.ok(ticketCategoryService.updateTicketCategory(request));
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<TicketCategoryDto> getTicketCategory(@PathVariable UUID categoryId) {
        return ResponseEntity.ok(ticketCategoryService.getTicketCategory(categoryId));
    }

    @GetMapping("/match/{matchId}")
    public ResponseEntity<List<TicketCategoryDto>> getCategoriesByMatch(@PathVariable UUID matchId) {
        return ResponseEntity.ok(ticketCategoryService.getCategoriesByMatch(matchId));
    }

    @GetMapping("/match/{matchId}/available")
    public ResponseEntity<List<TicketCategoryDto>> getAvailableCategoriesByMatch(@PathVariable UUID matchId) {
        return ResponseEntity.ok(ticketCategoryService.getAvailableCategoriesByMatch(matchId));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<TicketCategoryDto>> getCategoriesByCategory(@PathVariable Category category) {
        return ResponseEntity.ok(ticketCategoryService.getCategoriesByCategory(category));
    }

    @GetMapping("/match/{matchId}/price-asc")
    public ResponseEntity<List<TicketCategoryDto>> getCategoriesSortedByPriceAsc(@PathVariable UUID matchId) {
        return ResponseEntity.ok(ticketCategoryService.getCategoriesSortedByPriceAsc(matchId));
    }

    @GetMapping("/match/{matchId}/price-desc")
    public ResponseEntity<List<TicketCategoryDto>> getCategoriesSortedByPriceDesc(@PathVariable UUID matchId) {
        return ResponseEntity.ok(ticketCategoryService.getCategoriesSortedByPriceDesc(matchId));
    }

    @GetMapping("/price-range")
    public ResponseEntity<List<TicketCategoryDto>> getCategoriesByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice) {
        return ResponseEntity.ok(ticketCategoryService.getCategoriesByPriceRange(minPrice, maxPrice));
    }

    @GetMapping("/match/{matchId}/sold-out")
    public ResponseEntity<List<TicketCategoryDto>> getSoldOutCategories(@PathVariable UUID matchId) {
        return ResponseEntity.ok(ticketCategoryService.getSoldOutCategories(matchId));
    }

    @GetMapping("/match/{matchId}/partially-sold")
    public ResponseEntity<List<TicketCategoryDto>> getPartiallySoldCategories(@PathVariable UUID matchId) {
        return ResponseEntity.ok(ticketCategoryService.getPartiallySoldCategories(matchId));
    }

    @GetMapping("/{categoryId}/remaining-capacity")
    public ResponseEntity<Integer> getRemainingCapacity(@PathVariable UUID categoryId) {
        return ResponseEntity.ok(ticketCategoryService.getRemainingCapacity(categoryId));
    }

    @GetMapping("/{categoryId}/sold-count")
    public ResponseEntity<Integer> getSoldCount(@PathVariable UUID categoryId) {
        return ResponseEntity.ok(ticketCategoryService.getSoldCount(categoryId));
    }

    @GetMapping("/{categoryId}/sold-percentage")
    public ResponseEntity<Double> getSoldPercentage(@PathVariable UUID categoryId) {
        return ResponseEntity.ok(ticketCategoryService.getSoldPercentage(categoryId));
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteTicketCategory(@PathVariable UUID categoryId) {
        ticketCategoryService.deleteTicketCategory(categoryId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}