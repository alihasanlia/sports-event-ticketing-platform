package com.playtix.sports_event_ticketing_platform.api.controller;

import com.playtix.sports_event_ticketing_platform.domain.dto.match.CreateMatchRequest;
import com.playtix.sports_event_ticketing_platform.domain.dto.match.MatchDetailsDto;
import com.playtix.sports_event_ticketing_platform.domain.dto.match.MatchSummaryDto;
import com.playtix.sports_event_ticketing_platform.domain.entity.SportType;
import com.playtix.sports_event_ticketing_platform.service.MatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @PostMapping
    public ResponseEntity<MatchDetailsDto> createMatch(@Valid @RequestBody CreateMatchRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(matchService.createMatch(request));
    }

    @GetMapping("/{matchId}/summary")
    public ResponseEntity<MatchSummaryDto> getMatchSummary(@PathVariable UUID matchId) {
        return ResponseEntity.ok(matchService.getMatchSummary(matchId));
    }

    @GetMapping("/{matchId}/details")
    public ResponseEntity<MatchDetailsDto> getMatchDetails(@PathVariable UUID matchId) {
        return ResponseEntity.ok(matchService.getMatchDetails(matchId));
    }

    @GetMapping("/league/{leagueId}")
    public ResponseEntity<List<MatchSummaryDto>> getMatchesByLeague(@PathVariable UUID leagueId) {
        return ResponseEntity.ok(matchService.getMatchesByLeague(leagueId));
    }

    @GetMapping("/tournament/{tournamentId}")
    public ResponseEntity<List<MatchSummaryDto>> getMatchesByTournament(@PathVariable UUID tournamentId) {
        return ResponseEntity.ok(matchService.getMatchesByTournament(tournamentId));
    }

    @GetMapping("/stadium/{stadiumId}")
    public ResponseEntity<List<MatchSummaryDto>> getMatchesByStadium(@PathVariable UUID stadiumId) {
        return ResponseEntity.ok(matchService.getMatchesByStadium(stadiumId));
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<MatchSummaryDto>> getMatchesByTeam(@PathVariable UUID teamId) {
        return ResponseEntity.ok(matchService.getMatchesByTeam(teamId));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<MatchSummaryDto>> getUpcomingMatches() {
        return ResponseEntity.ok(matchService.getUpcomingMatches());
    }

    @GetMapping("/finished")
    public ResponseEntity<List<MatchSummaryDto>> getFinishedMatches() {
        return ResponseEntity.ok(matchService.getFinishedMatches());
    }

    @GetMapping("/sport/{sportType}")
    public ResponseEntity<List<MatchSummaryDto>> getMatchesBySportType(@PathVariable SportType sportType) {
        return ResponseEntity.ok(matchService.getMatchesBySportType(sportType));
    }

    @GetMapping("/sport/{sportType}/upcoming")
    public ResponseEntity<List<MatchSummaryDto>> getUpcomingMatchesBySportType(@PathVariable SportType sportType) {
        return ResponseEntity.ok(matchService.getUpcomingMatchesBySportType(sportType));
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<MatchSummaryDto>> getMatchesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(matchService.getMatchesByDateRange(start, end));
    }

    @GetMapping("/league/{leagueId}/date-range")
    public ResponseEntity<List<MatchSummaryDto>> getMatchesByLeagueAndDateRange(
            @PathVariable UUID leagueId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(matchService.getMatchesByLeagueAndDateRange(leagueId, start, end));
    }

    @GetMapping("/league/{leagueId}/upcoming-count")
    public ResponseEntity<Long> countUpcomingMatchesByLeague(@PathVariable UUID leagueId) {
        return ResponseEntity.ok(matchService.countUpcomingMatchesByLeague(leagueId));
    }

    @GetMapping("/tournament/{tournamentId}/upcoming-count")
    public ResponseEntity<Long> countUpcomingMatchesByTournament(@PathVariable UUID tournamentId) {
        return ResponseEntity.ok(matchService.countUpcomingMatchesByTournament(tournamentId));
    }

    @DeleteMapping("/{matchId}")
    public ResponseEntity<Void> deleteMatch(@PathVariable UUID matchId) {
        matchService.deleteMatch(matchId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}