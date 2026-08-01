package com.playtix.sports_event_ticketing_platform.api.controller;

import com.playtix.sports_event_ticketing_platform.domain.dto.league.LeagueCreateRequestDTO;
import com.playtix.sports_event_ticketing_platform.domain.dto.league.LeagueResponseDTO;
import com.playtix.sports_event_ticketing_platform.domain.dto.league.LeagueSummaryDTO;
import com.playtix.sports_event_ticketing_platform.domain.dto.league.LeagueUpdateRequestDTO;
import com.playtix.sports_event_ticketing_platform.service.LeagueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/leagues")
@RequiredArgsConstructor
public class LeagueController {

    private final LeagueService leagueService;

    @PostMapping
    public ResponseEntity<LeagueResponseDTO> createLeague(@Valid @RequestBody LeagueCreateRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(leagueService.createLeague(request));
    }

    @PutMapping
    public ResponseEntity<LeagueResponseDTO> updateLeague(@Valid @RequestBody LeagueUpdateRequestDTO request) {
        return ResponseEntity.ok(leagueService.updateLeague(request));
    }

    @GetMapping("/{leagueId}")
    public ResponseEntity<LeagueResponseDTO> getLeague(@PathVariable UUID leagueId) {
        return ResponseEntity.ok(leagueService.getLeague(leagueId));
    }

    @GetMapping("/{leagueId}/summary")
    public ResponseEntity<LeagueSummaryDTO> getLeagueSummary(@PathVariable UUID leagueId) {
        return ResponseEntity.ok(leagueService.getLeagueSummary(leagueId));
    }

    @GetMapping
    public ResponseEntity<List<LeagueResponseDTO>> getAllLeagues() {
        return ResponseEntity.ok(leagueService.getAllLeagues());
    }

    @GetMapping("/summaries")
    public ResponseEntity<List<LeagueSummaryDTO>> getAllLeagueSummaries() {
        return ResponseEntity.ok(leagueService.getAllLeagueSummaries());
    }

    @GetMapping("/sport/{sportId}")
    public ResponseEntity<List<LeagueResponseDTO>> getLeaguesBySport(@PathVariable UUID sportId) {
        return ResponseEntity.ok(leagueService.getLeaguesBySport(sportId));
    }

    @GetMapping("/sport/{sportId}/summaries")
    public ResponseEntity<List<LeagueSummaryDTO>> getLeagueSummariesBySport(@PathVariable UUID sportId) {
        return ResponseEntity.ok(leagueService.getLeagueSummariesBySport(sportId));
    }

    @GetMapping("/sorted-by-name")
    public ResponseEntity<List<LeagueResponseDTO>> getLeaguesSortedByName() {
        return ResponseEntity.ok(leagueService.getLeaguesSortedByName());
    }

    @GetMapping("/country/{country}")
    public ResponseEntity<List<LeagueResponseDTO>> getLeaguesByCountry(@PathVariable String country) {
        return ResponseEntity.ok(leagueService.getLeaguesByCountry(country));
    }

    @GetMapping("/season/{season}")
    public ResponseEntity<List<LeagueResponseDTO>> getLeaguesBySeason(@PathVariable String season) {
        return ResponseEntity.ok(leagueService.getLeaguesBySeason(season));
    }

    @GetMapping("/with-matches")
    public ResponseEntity<List<LeagueResponseDTO>> getLeaguesWithMatches() {
        return ResponseEntity.ok(leagueService.getLeaguesWithMatches());
    }

    @GetMapping("/without-matches")
    public ResponseEntity<List<LeagueResponseDTO>> getLeaguesWithoutMatches() {
        return ResponseEntity.ok(leagueService.getLeaguesWithoutMatches());
    }

    @GetMapping("/with-upcoming-matches")
    public ResponseEntity<List<LeagueResponseDTO>> getLeaguesWithUpcomingMatches() {
        return ResponseEntity.ok(leagueService.getLeaguesWithUpcomingMatches());
    }

    @GetMapping("/with-finished-matches")
    public ResponseEntity<List<LeagueResponseDTO>> getLeaguesWithFinishedMatches() {
        return ResponseEntity.ok(leagueService.getLeaguesWithFinishedMatches());
    }

    @GetMapping("/search")
    public ResponseEntity<List<LeagueResponseDTO>> searchLeaguesByNameOrCountry(@RequestParam String keyword) {
        return ResponseEntity.ok(leagueService.searchLeaguesByNameOrCountry(keyword));
    }

    @GetMapping("/{leagueId}/matches-count")
    public ResponseEntity<Long> countMatchesByLeague(@PathVariable UUID leagueId) {
        return ResponseEntity.ok(leagueService.countMatchesByLeague(leagueId));
    }

    @GetMapping("/{leagueId}/upcoming-matches-count")
    public ResponseEntity<Long> countUpcomingMatchesByLeague(@PathVariable UUID leagueId) {
        return ResponseEntity.ok(leagueService.countUpcomingMatchesByLeague(leagueId));
    }

    @GetMapping("/{leagueId}/finished-matches-count")
    public ResponseEntity<Long> countFinishedMatchesByLeague(@PathVariable UUID leagueId) {
        return ResponseEntity.ok(leagueService.countFinishedMatchesByLeague(leagueId));
    }

    @GetMapping("/teams-range")
    public ResponseEntity<List<LeagueResponseDTO>> getLeaguesByNumberOfTeamsRange(
            @RequestParam int minTeams,
            @RequestParam int maxTeams) {
        return ResponseEntity.ok(leagueService.getLeaguesByNumberOfTeamsRange(minTeams, maxTeams));
    }

    @GetMapping("/most-teams")
    public ResponseEntity<List<LeagueResponseDTO>> getLeaguesWithMostTeams() {
        return ResponseEntity.ok(leagueService.getLeaguesWithMostTeams());
    }

    @GetMapping("/fewest-teams")
    public ResponseEntity<List<LeagueResponseDTO>> getLeaguesWithFewestTeams() {
        return ResponseEntity.ok(leagueService.getLeaguesWithFewestTeams());
    }

    @GetMapping("/average-teams")
    public ResponseEntity<Double> getAverageNumberOfTeams() {
        return ResponseEntity.ok(leagueService.getAverageNumberOfTeams());
    }

    @DeleteMapping("/{leagueId}")
    public ResponseEntity<Void> deleteLeague(@PathVariable UUID leagueId) {
        leagueService.deleteLeague(leagueId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}