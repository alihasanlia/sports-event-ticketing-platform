package com.playtix.sports_event_ticketing_platform.api.controller;

import com.playtix.sports_event_ticketing_platform.domain.dto.sport.CreateSportRequest;
import com.playtix.sports_event_ticketing_platform.domain.dto.sport.SportDto;
import com.playtix.sports_event_ticketing_platform.domain.dto.sport.UpdateSportRequest;
import com.playtix.sports_event_ticketing_platform.domain.entity.SportType;
import com.playtix.sports_event_ticketing_platform.service.SportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sports")
@RequiredArgsConstructor
public class SportController {

    private final SportService sportService;

    @PostMapping
    public ResponseEntity<SportDto> createSport(@Valid @RequestBody CreateSportRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sportService.createSport(request));
    }

    @PutMapping
    public ResponseEntity<SportDto> updateSport(@Valid @RequestBody UpdateSportRequest request) {
        return ResponseEntity.ok(sportService.updateSport(request));
    }

    @GetMapping("/{sportId}")
    public ResponseEntity<SportDto> getSport(@PathVariable UUID sportId) {
        return ResponseEntity.ok(sportService.getSport(sportId));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<SportDto> getSportByName(@PathVariable SportType name) {
        return ResponseEntity.ok(sportService.getSportByName(name));
    }

    @GetMapping
    public ResponseEntity<List<SportDto>> getAllSports() {
        return ResponseEntity.ok(sportService.getAllSports());
    }

    @GetMapping("/sorted-by-name")
    public ResponseEntity<List<SportDto>> getSportsSortedByName() {
        return ResponseEntity.ok(sportService.getSportsSortedByName());
    }

    @GetMapping("/with-leagues")
    public ResponseEntity<List<SportDto>> getSportsWithLeagues() {
        return ResponseEntity.ok(sportService.getSportsWithLeagues());
    }

    @GetMapping("/with-tournaments")
    public ResponseEntity<List<SportDto>> getSportsWithTournaments() {
        return ResponseEntity.ok(sportService.getSportsWithTournaments());
    }

    @GetMapping("/without-leagues")
    public ResponseEntity<List<SportDto>> getSportsWithoutLeagues() {
        return ResponseEntity.ok(sportService.getSportsWithoutLeagues());
    }

    @GetMapping("/without-tournaments")
    public ResponseEntity<List<SportDto>> getSportsWithoutTournaments() {
        return ResponseEntity.ok(sportService.getSportsWithoutTournaments());
    }

    @GetMapping("/with-both")
    public ResponseEntity<List<SportDto>> getSportsWithBothLeaguesAndTournaments() {
        return ResponseEntity.ok(sportService.getSportsWithBothLeaguesAndTournaments());
    }

    @GetMapping("/with-leagues-or-tournaments")
    public ResponseEntity<List<SportDto>> getSportsWithLeaguesOrTournaments() {
        return ResponseEntity.ok(sportService.getSportsWithLeaguesOrTournaments());
    }

    @GetMapping("/sorted-by-players")
    public ResponseEntity<List<SportDto>> getSportsSortedByPlayers() {
        return ResponseEntity.ok(sportService.getSportsSortedByPlayers());
    }

    @GetMapping("/players-range")
    public ResponseEntity<List<SportDto>> getSportsByPlayersRange(
            @RequestParam int minPlayers,
            @RequestParam int maxPlayers) {
        return ResponseEntity.ok(sportService.getSportsByPlayersRange(minPlayers, maxPlayers));
    }

    @GetMapping("/search")
    public ResponseEntity<List<SportDto>> searchSportsByDescription(@RequestParam String keyword) {
        return ResponseEntity.ok(sportService.searchSportsByDescription(keyword));
    }

    @GetMapping("/{sportId}/leagues-count")
    public ResponseEntity<Long> countLeaguesBySport(@PathVariable UUID sportId) {
        return ResponseEntity.ok(sportService.countLeaguesBySport(sportId));
    }

    @GetMapping("/{sportId}/tournaments-count")
    public ResponseEntity<Long> countTournamentsBySport(@PathVariable UUID sportId) {
        return ResponseEntity.ok(sportService.countTournamentsBySport(sportId));
    }

    @GetMapping("/average-players")
    public ResponseEntity<Double> getAveragePlayers() {
        return ResponseEntity.ok(sportService.getAveragePlayers());
    }

    @GetMapping("/above-average-players")
    public ResponseEntity<List<SportDto>> getSportsAboveAveragePlayers() {
        return ResponseEntity.ok(sportService.getSportsAboveAveragePlayers());
    }

    @GetMapping("/below-average-players")
    public ResponseEntity<List<SportDto>> getSportsBelowAveragePlayers() {
        return ResponseEntity.ok(sportService.getSportsBelowAveragePlayers());
    }

    @DeleteMapping("/{sportId}")
    public ResponseEntity<Void> deleteSport(@PathVariable UUID sportId) {
        sportService.deleteSport(sportId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}