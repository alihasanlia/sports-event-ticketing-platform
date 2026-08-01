package com.playtix.sports_event_ticketing_platform.service;

import com.playtix.sports_event_ticketing_platform.domain.dto.match.CreateMatchRequest;
import com.playtix.sports_event_ticketing_platform.domain.dto.match.MatchDetailsDto;
import com.playtix.sports_event_ticketing_platform.domain.dto.match.MatchSummaryDto;
import com.playtix.sports_event_ticketing_platform.domain.entity.League;
import com.playtix.sports_event_ticketing_platform.domain.entity.SportType;
import com.playtix.sports_event_ticketing_platform.domain.entity.Stadium;
import com.playtix.sports_event_ticketing_platform.domain.entity.Team;
import com.playtix.sports_event_ticketing_platform.domain.entity.match.Match;
import com.playtix.sports_event_ticketing_platform.domain.entity.tournament.Tournament;
import com.playtix.sports_event_ticketing_platform.repository.LeagueRepository;
import com.playtix.sports_event_ticketing_platform.repository.MatchRepository;
import com.playtix.sports_event_ticketing_platform.repository.StadiumRepository;
import com.playtix.sports_event_ticketing_platform.repository.TeamRepository;
import com.playtix.sports_event_ticketing_platform.repository.TournamentRepository;
import com.playtix.sports_event_ticketing_platform.mapper.MatchMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;
    private final TeamRepository teamRepository;
    private final StadiumRepository stadiumRepository;
    private final LeagueRepository leagueRepository;
    private final TournamentRepository tournamentRepository;
    private final MatchMapper matchMapper;

    @Transactional
    public MatchDetailsDto createMatch(CreateMatchRequest request) {
        Team homeTeam = teamRepository.findById(request.homeTeamId())
                .orElseThrow(() -> new RuntimeException("Home team not found"));

        Team awayTeam = teamRepository.findById(request.awayTeamId())
                .orElseThrow(() -> new RuntimeException("Away team not found"));

        Stadium stadium = stadiumRepository.findById(request.stadiumId())
                .orElseThrow(() -> new RuntimeException("Stadium not found"));

        if (homeTeam.getId().equals(awayTeam.getId())) {
            throw new RuntimeException("Home team and away team cannot be the same");
        }

        Match match = Match.builder()
                .matchDate(request.matchDate())
                .sportType(request.sportType())
                .homeTeam(homeTeam)
                .awayTeam(awayTeam)
                .stadium(stadium)
                .build();

        if (request.leagueId() != null) {
            League league = leagueRepository.findById(request.leagueId())
                    .orElseThrow(() -> new RuntimeException("League not found"));
            match.setLeague(league);
        }

        if (request.tournamentId() != null) {
            Tournament tournament = tournamentRepository.findById(request.tournamentId())
                    .orElseThrow(() -> new RuntimeException("Tournament not found"));
            match.setTournament(tournament);
        }

        match = matchRepository.save(match);
        return matchMapper.toDetailsDto(match);
    }

    @Transactional(readOnly = true)
    public MatchSummaryDto getMatchSummary(UUID matchId) {
        Match match = matchRepository.findByIdWithAllRelationships(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found"));
        return matchMapper.toSummaryDto(match);
    }

    @Transactional(readOnly = true)
    public MatchDetailsDto getMatchDetails(UUID matchId) {
        Match match = matchRepository.findByIdWithAllRelationships(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found"));
        return matchMapper.toDetailsDto(match);
    }

    @Transactional(readOnly = true)
    public List<MatchSummaryDto> getMatchesByLeague(UUID leagueId) {
        List<Match> matches = matchRepository.findByLeague_Id(leagueId);
        return matchMapper.toSummaryDtoList(matches);
    }

    @Transactional(readOnly = true)
    public List<MatchSummaryDto> getMatchesByTournament(UUID tournamentId) {
        List<Match> matches = matchRepository.findByTournament_Id(tournamentId);
        return matchMapper.toSummaryDtoList(matches);
    }

    @Transactional(readOnly = true)
    public List<MatchSummaryDto> getMatchesByStadium(UUID stadiumId) {
        List<Match> matches = matchRepository.findByStadium_Id(stadiumId);
        return matchMapper.toSummaryDtoList(matches);
    }

    @Transactional(readOnly = true)
    public List<MatchSummaryDto> getMatchesByTeam(UUID teamId) {
        List<Match> matches = matchRepository.findByHomeTeam_IdOrAwayTeam_Id(teamId, teamId);
        return matchMapper.toSummaryDtoList(matches);
    }

    @Transactional(readOnly = true)
    public List<MatchSummaryDto> getUpcomingMatches() {
        List<Match> matches = matchRepository.findUpcomingMatches(LocalDateTime.now());
        return matchMapper.toSummaryDtoList(matches);
    }

    @Transactional(readOnly = true)
    public List<MatchSummaryDto> getFinishedMatches() {
        List<Match> matches = matchRepository.findFinishedMatches(LocalDateTime.now());
        return matchMapper.toSummaryDtoList(matches);
    }

    @Transactional(readOnly = true)
    public List<MatchSummaryDto> getMatchesBySportType(SportType sportType) {
        List<Match> matches = matchRepository.findBySportType(sportType);
        return matchMapper.toSummaryDtoList(matches);
    }

    @Transactional(readOnly = true)
    public List<MatchSummaryDto> getUpcomingMatchesBySportType(SportType sportType) {
        List<Match> matches = matchRepository.findUpcomingMatchesBySportType(sportType, LocalDateTime.now());
        return matchMapper.toSummaryDtoList(matches);
    }

    @Transactional(readOnly = true)
    public List<MatchSummaryDto> getMatchesByDateRange(LocalDateTime start, LocalDateTime end) {
        List<Match> matches = matchRepository.findByMatchDateBetween(start, end);
        return matchMapper.toSummaryDtoList(matches);
    }

    @Transactional(readOnly = true)
    public List<MatchSummaryDto> getMatchesByLeagueAndDateRange(UUID leagueId, LocalDateTime start, LocalDateTime end) {
        List<Match> matches = matchRepository.findByLeague_Id(leagueId);
        return matches.stream()
                .filter(m -> m.getMatchDate() != null && 
                        !m.getMatchDate().isBefore(start) && 
                        !m.getMatchDate().isAfter(end))
                .map(matchMapper::toSummaryDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public long countUpcomingMatchesByLeague(UUID leagueId) {
        return matchRepository.countUpcomingMatchesByLeagueId(leagueId, LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public long countUpcomingMatchesByTournament(UUID tournamentId) {
        return matchRepository.countUpcomingMatchesByTournamentId(tournamentId, LocalDateTime.now());
    }

    @Transactional
    public void deleteMatch(UUID matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found"));
        
        if (!match.getTicketCategories().isEmpty()) {
            throw new RuntimeException("Cannot delete match with existing ticket categories");
        }
        
        matchRepository.delete(match);
    }
}