package com.playtix.sports_event_ticketing_platform.api.controller;

import com.playtix.sports_event_ticketing_platform.domain.dto.tournament.TournamentCreateRequestDTO;
import com.playtix.sports_event_ticketing_platform.domain.dto.tournament.TournamentResponseDTO;
import com.playtix.sports_event_ticketing_platform.domain.dto.tournament.TournamentStatusUpdateRequestDTO;
import com.playtix.sports_event_ticketing_platform.domain.dto.tournament.TournamentSummaryDTO;
import com.playtix.sports_event_ticketing_platform.domain.dto.tournament.TournamentUpdateRequestDTO;
import com.playtix.sports_event_ticketing_platform.domain.entity.tournament.TournamentStatus;
import com.playtix.sports_event_ticketing_platform.service.TournamentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tournaments")
@RequiredArgsConstructor
public class TournamentController {

    private final TournamentService tournamentService;

    @PostMapping
    public ResponseEntity<TournamentResponseDTO> createTournament(@Valid @RequestBody TournamentCreateRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tournamentService.createTournament(request));
    }

    @PutMapping
    public ResponseEntity<TournamentResponseDTO> updateTournament(@Valid @RequestBody TournamentUpdateRequestDTO request) {
        return ResponseEntity.ok(tournamentService.updateTournament(request));
    }

    @PatchMapping("/{tournamentId}/status")
    public ResponseEntity<TournamentResponseDTO> updateStatus(
            @PathVariable UUID tournamentId,
            @Valid @RequestBody TournamentStatusUpdateRequestDTO request) {
        return ResponseEntity.ok(tournamentService.updateStatus(request, tournamentId));
    }

    @PostMapping("/{tournamentId}/start")
    public ResponseEntity<TournamentResponseDTO> startTournament(@PathVariable UUID tournamentId) {
        return ResponseEntity.ok(tournamentService.startTournament(tournamentId));
    }

    @PostMapping("/{tournamentId}/finish")
    public ResponseEntity<TournamentResponseDTO> finishTournament(@PathVariable UUID tournamentId) {
        return ResponseEntity.ok(tournamentService.finishTournament(tournamentId));
    }

    @PostMapping("/{tournamentId}/cancel")
    public ResponseEntity<TournamentResponseDTO> cancelTournament(@PathVariable UUID tournamentId) {
        return ResponseEntity.ok(tournamentService.cancelTournament(tournamentId));
    }

    @GetMapping("/{tournamentId}")
    public ResponseEntity<TournamentResponseDTO> getTournament(@PathVariable UUID tournamentId) {
        return ResponseEntity.ok(tournamentService.getTournament(tournamentId));
    }

    @GetMapping("/{tournamentId}/summary")
    public ResponseEntity<TournamentSummaryDTO> getTournamentSummary(@PathVariable UUID tournamentId) {
        return ResponseEntity.ok(tournamentService.getTournamentSummary(tournamentId));
    }

    @GetMapping
    public ResponseEntity<List<TournamentResponseDTO>> getAllTournaments() {
        return ResponseEntity.ok(tournamentService.getAllTournaments());
    }

    @GetMapping("/summaries")
    public ResponseEntity<List<TournamentSummaryDTO>> getAllTournamentSummaries() {
        return ResponseEntity.ok(tournamentService.getAllTournamentSummaries());
    }

    @GetMapping("/sport/{sportId}")
    public ResponseEntity<List<TournamentResponseDTO>> getTournamentsBySport(@PathVariable UUID sportId) {
        return ResponseEntity.ok(tournamentService.getTournamentsBySport(sportId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<TournamentResponseDTO>> getTournamentsByStatus(@PathVariable TournamentStatus status) {
        return ResponseEntity.ok(tournamentService.getTournamentsByStatus(status));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<TournamentResponseDTO>> getUpcomingTournaments() {
        return ResponseEntity.ok(tournamentService.getUpcomingTournaments());
    }

    @GetMapping("/active")
    public ResponseEntity<List<TournamentResponseDTO>> getActiveTournaments() {
        return ResponseEntity.ok(tournamentService.getActiveTournaments());
    }

    @GetMapping("/finished")
    public ResponseEntity<List<TournamentResponseDTO>> getFinishedTournaments() {
        return ResponseEntity.ok(tournamentService.getFinishedTournaments());
    }

    @GetMapping("/cancelled")
    public ResponseEntity<List<TournamentResponseDTO>> getCancelledTournaments() {
        return ResponseEntity.ok(tournamentService.getCancelledTournaments());
    }

    @GetMapping("/with-matches")
    public ResponseEntity<List<TournamentResponseDTO>> getTournamentsWithMatches() {
        return ResponseEntity.ok(tournamentService.getTournamentsWithMatches());
    }

    @GetMapping("/without-matches")
    public ResponseEntity<List<TournamentResponseDTO>> getTournamentsWithoutMatches() {
        return ResponseEntity.ok(tournamentService.getTournamentsWithoutMatches());
    }

    @GetMapping("/search")
    public ResponseEntity<List<TournamentResponseDTO>> searchTournamentsByName(@RequestParam String name) {
        return ResponseEntity.ok(tournamentService.searchTournamentsByName(name));
    }

    @GetMapping("/sorted-by-name")
    public ResponseEntity<List<TournamentResponseDTO>> getTournamentsSortedByName() {
        return ResponseEntity.ok(tournamentService.getTournamentsSortedByName());
    }

    @GetMapping("/sorted-by-date")
    public ResponseEntity<List<TournamentResponseDTO>> getTournamentsSortedByDate() {
        return ResponseEntity.ok(tournamentService.getTournamentsSortedByDate());
    }

    @GetMapping("/latest")
    public ResponseEntity<List<TournamentResponseDTO>> getLatestTournaments() {
        return ResponseEntity.ok(tournamentService.getLatestTournaments());
    }

    @GetMapping("/{tournamentId}/matches-count")
    public ResponseEntity<Long> countMatchesByTournament(@PathVariable UUID tournamentId) {
        return ResponseEntity.ok(tournamentService.countMatchesByTournament(tournamentId));
    }

    @GetMapping("/{tournamentId}/upcoming-matches-count")
    public ResponseEntity<Long> countUpcomingMatchesByTournament(@PathVariable UUID tournamentId) {
        return ResponseEntity.ok(tournamentService.countUpcomingMatchesByTournament(tournamentId));
    }

    @GetMapping("/{tournamentId}/finished-matches-count")
    public ResponseEntity<Long> countFinishedMatchesByTournament(@PathVariable UUID tournamentId) {
        return ResponseEntity.ok(tournamentService.countFinishedMatchesByTournament(tournamentId));
    }

    @DeleteMapping("/{tournamentId}")
    public ResponseEntity<Void> deleteTournament(@PathVariable UUID tournamentId) {
        tournamentService.deleteTournament(tournamentId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}