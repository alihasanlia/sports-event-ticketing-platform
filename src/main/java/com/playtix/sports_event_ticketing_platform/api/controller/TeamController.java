package com.playtix.sports_event_ticketing_platform.api.controller;

import com.playtix.sports_event_ticketing_platform.domain.dto.team.CreateTeamRequest;
import com.playtix.sports_event_ticketing_platform.domain.dto.team.TeamDto;
import com.playtix.sports_event_ticketing_platform.domain.dto.team.UpdateTeamRequest;
import com.playtix.sports_event_ticketing_platform.service.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @PostMapping
    public ResponseEntity<TeamDto> createTeam(@Valid @RequestBody CreateTeamRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(teamService.createTeam(request));
    }

    @PutMapping
    public ResponseEntity<TeamDto> updateTeam(@Valid @RequestBody UpdateTeamRequest request) {
        return ResponseEntity.ok(teamService.updateTeam(request));
    }

    @GetMapping("/{teamId}")
    public ResponseEntity<TeamDto> getTeam(@PathVariable UUID teamId) {
        return ResponseEntity.ok(teamService.getTeam(teamId));
    }

    @GetMapping
    public ResponseEntity<List<TeamDto>> getAllTeams() {
        return ResponseEntity.ok(teamService.getAllTeams());
    }

    @GetMapping("/search")
    public ResponseEntity<List<TeamDto>> searchTeamsByName(@RequestParam String name) {
        return ResponseEntity.ok(teamService.searchTeamsByName(name));
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<List<TeamDto>> getTeamsByCity(@PathVariable String city) {
        return ResponseEntity.ok(teamService.getTeamsByCity(city));
    }

    @GetMapping("/coach/{coach}")
    public ResponseEntity<List<TeamDto>> getTeamsByCoach(@PathVariable String coach) {
        return ResponseEntity.ok(teamService.getTeamsByCoach(coach));
    }

    @GetMapping("/founded-year-range")
    public ResponseEntity<List<TeamDto>> getTeamsByFoundedYearRange(
            @RequestParam int startYear,
            @RequestParam int endYear) {
        return ResponseEntity.ok(teamService.getTeamsByFoundedYearRange(startYear, endYear));
    }

    @GetMapping("/sorted-by-name")
    public ResponseEntity<List<TeamDto>> getTeamsSortedByName() {
        return ResponseEntity.ok(teamService.getTeamsSortedByName());
    }

    @GetMapping("/sorted-by-founded-year")
    public ResponseEntity<List<TeamDto>> getTeamsSortedByFoundedYear() {
        return ResponseEntity.ok(teamService.getTeamsSortedByFoundedYear());
    }

    @GetMapping("/with-matches")
    public ResponseEntity<List<TeamDto>> getTeamsWithMatches() {
        return ResponseEntity.ok(teamService.getTeamsWithMatches());
    }

    @GetMapping("/without-matches")
    public ResponseEntity<List<TeamDto>> getTeamsWithoutMatches() {
        return ResponseEntity.ok(teamService.getTeamsWithoutMatches());
    }

    @GetMapping("/with-upcoming-matches")
    public ResponseEntity<List<TeamDto>> getTeamsWithUpcomingMatches() {
        return ResponseEntity.ok(teamService.getTeamsWithUpcomingMatches());
    }

    @GetMapping("/{teamId}/matches-count")
    public ResponseEntity<Long> countAllMatchesByTeam(@PathVariable UUID teamId) {
        return ResponseEntity.ok(teamService.countAllMatchesByTeam(teamId));
    }

    @GetMapping("/{teamId}/upcoming-matches-count")
    public ResponseEntity<Long> countUpcomingMatchesByTeam(@PathVariable UUID teamId) {
        return ResponseEntity.ok(teamService.countUpcomingMatchesByTeam(teamId));
    }

    @GetMapping("/average-founded-year")
    public ResponseEntity<Double> getAverageFoundedYear() {
        return ResponseEntity.ok(teamService.getAverageFoundedYear());
    }

    @GetMapping("/latest-founded-year")
    public ResponseEntity<Integer> getLatestFoundedYear() {
        return ResponseEntity.ok(teamService.getLatestFoundedYear());
    }

    @GetMapping("/earliest-founded-year")
    public ResponseEntity<Integer> getEarliestFoundedYear() {
        return ResponseEntity.ok(teamService.getEarliestFoundedYear());
    }

    @DeleteMapping("/{teamId}")
    public ResponseEntity<Void> deleteTeam(@PathVariable UUID teamId) {
        teamService.deleteTeam(teamId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}