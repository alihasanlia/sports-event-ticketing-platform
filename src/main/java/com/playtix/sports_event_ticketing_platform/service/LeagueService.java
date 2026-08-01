package com.playtix.sports_event_ticketing_platform.service;

import com.playtix.sports_event_ticketing_platform.domain.dto.league.LeagueCreateRequestDTO;
import com.playtix.sports_event_ticketing_platform.domain.dto.league.LeagueResponseDTO;
import com.playtix.sports_event_ticketing_platform.domain.dto.league.LeagueSummaryDTO;
import com.playtix.sports_event_ticketing_platform.domain.dto.league.LeagueUpdateRequestDTO;
import com.playtix.sports_event_ticketing_platform.domain.entity.League;
import com.playtix.sports_event_ticketing_platform.domain.entity.Sport;
import com.playtix.sports_event_ticketing_platform.repository.LeagueRepository;
import com.playtix.sports_event_ticketing_platform.repository.SportRepository;
import com.playtix.sports_event_ticketing_platform.mapper.LeagueMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LeagueService {

    private final LeagueRepository leagueRepository;
    private final SportRepository sportRepository;
    private final LeagueMapper leagueMapper;

    @Transactional
    public LeagueResponseDTO createLeague(LeagueCreateRequestDTO request) {
        if (leagueRepository.existsByNameIgnoreCase(request.name())) {
            throw new RuntimeException("League already exists with this name");
        }

        Sport sport = sportRepository.findById(request.sportId())
                .orElseThrow(() -> new RuntimeException("Sport not found"));

        League league = League.builder()
                .name(request.name())
                .country(request.country())
                .season(request.season())
                .numberOfTeams(request.numberOfTeams())
                .description(request.description())
                .sport(sport)
                .build();

        league = leagueRepository.save(league);
        return leagueMapper.toResponseDTO(league);
    }

    @Transactional
    public LeagueResponseDTO updateLeague(LeagueUpdateRequestDTO request) {
        League league = leagueRepository.findById(request.id())
                .orElseThrow(() -> new RuntimeException("League not found"));

        if (!league.getName().equals(request.name()) && 
            leagueRepository.existsByNameIgnoreCase(request.name())) {
            throw new RuntimeException("League already exists with this name");
        }

        league.setName(request.name());
        league.setCountry(request.country());
        league.setSeason(request.season());
        league.setNumberOfTeams(request.numberOfTeams());
        league.setDescription(request.description());

        if (request.sportId() != null) {
            Sport sport = sportRepository.findById(request.sportId())
                    .orElseThrow(() -> new RuntimeException("Sport not found"));
            league.setSport(sport);
        }

        league = leagueRepository.save(league);
        return leagueMapper.toResponseDTO(league);
    }

    @Transactional(readOnly = true)
    public LeagueResponseDTO getLeague(UUID leagueId) {
        League league = leagueRepository.findByIdWithAllRelationships(leagueId)
                .orElseThrow(() -> new RuntimeException("League not found"));
        return leagueMapper.toResponseDTO(league);
    }

    @Transactional(readOnly = true)
    public LeagueSummaryDTO getLeagueSummary(UUID leagueId) {
        League league = leagueRepository.findById(leagueId)
                .orElseThrow(() -> new RuntimeException("League not found"));
        return leagueMapper.toSummaryDTO(league);
    }

    @Transactional(readOnly = true)
    public List<LeagueResponseDTO> getAllLeagues() {
        List<League> leagues = leagueRepository.findAll();
        return leagueMapper.toResponseDTOList(leagues);
    }

    @Transactional(readOnly = true)
    public List<LeagueSummaryDTO> getAllLeagueSummaries() {
        List<League> leagues = leagueRepository.findAll();
        return leagueMapper.toSummaryDTOList(leagues);
    }

    @Transactional(readOnly = true)
    public List<LeagueResponseDTO> getLeaguesBySport(UUID sportId) {
        List<League> leagues = leagueRepository.findBySport_Id(sportId);
        return leagueMapper.toResponseDTOList(leagues);
    }

    @Transactional(readOnly = true)
    public List<LeagueSummaryDTO> getLeagueSummariesBySport(UUID sportId) {
        List<League> leagues = leagueRepository.findBySport_Id(sportId);
        return leagueMapper.toSummaryDTOList(leagues);
    }

    @Transactional(readOnly = true)
    public List<LeagueResponseDTO> getLeaguesSortedByName() {
        List<League> leagues = leagueRepository.findAllByOrderByNameAsc();
        return leagueMapper.toResponseDTOList(leagues);
    }

    @Transactional(readOnly = true)
    public List<LeagueResponseDTO> getLeaguesByCountry(String country) {
        List<League> leagues = leagueRepository.findByCountry(country);
        return leagueMapper.toResponseDTOList(leagues);
    }

    @Transactional(readOnly = true)
    public List<LeagueResponseDTO> getLeaguesBySeason(String season) {
        List<League> leagues = leagueRepository.findBySeason(season);
        return leagueMapper.toResponseDTOList(leagues);
    }

    @Transactional(readOnly = true)
    public List<LeagueResponseDTO> getLeaguesWithMatches() {
        List<League> leagues = leagueRepository.findLeaguesWithAnyMatch();
        return leagueMapper.toResponseDTOList(leagues);
    }

    @Transactional(readOnly = true)
    public List<LeagueResponseDTO> getLeaguesWithoutMatches() {
        List<League> leagues = leagueRepository.findLeaguesWithoutMatches();
        return leagueMapper.toResponseDTOList(leagues);
    }

    @Transactional(readOnly = true)
    public List<LeagueResponseDTO> getLeaguesWithUpcomingMatches() {
        List<League> leagues = leagueRepository.findLeaguesWithUpcomingMatches(LocalDateTime.now());
        return leagueMapper.toResponseDTOList(leagues);
    }

    @Transactional(readOnly = true)
    public List<LeagueResponseDTO> getLeaguesWithFinishedMatches() {
        List<League> leagues = leagueRepository.findLeaguesWithFinishedMatches(LocalDateTime.now());
        return leagueMapper.toResponseDTOList(leagues);
    }

    @Transactional(readOnly = true)
    public List<LeagueResponseDTO> searchLeaguesByNameOrCountry(String keyword) {
        List<League> leagues = leagueRepository.searchByNameOrCountry(keyword);
        return leagueMapper.toResponseDTOList(leagues);
    }

    @Transactional(readOnly = true)
    public long countMatchesByLeague(UUID leagueId) {
        return leagueRepository.countAllMatchesByLeagueId(leagueId);
    }

    @Transactional(readOnly = true)
    public long countUpcomingMatchesByLeague(UUID leagueId) {
        return leagueRepository.countUpcomingMatchesByLeagueId(leagueId, LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public long countFinishedMatchesByLeague(UUID leagueId) {
        return leagueRepository.countFinishedMatchesByLeagueId(leagueId, LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public List<LeagueResponseDTO> getLeaguesByNumberOfTeamsRange(int minTeams, int maxTeams) {
        List<League> leagues = leagueRepository.findByNumberOfTeamsBetween(minTeams, maxTeams);
        return leagueMapper.toResponseDTOList(leagues);
    }

    @Transactional(readOnly = true)
    public List<LeagueResponseDTO> getLeaguesWithMostTeams() {
        List<League> leagues = leagueRepository.findLeaguesWithMostTeams();
        return leagueMapper.toResponseDTOList(leagues);
    }

    @Transactional(readOnly = true)
    public List<LeagueResponseDTO> getLeaguesWithFewestTeams() {
        List<League> leagues = leagueRepository.findLeaguesWithFewestTeams();
        return leagueMapper.toResponseDTOList(leagues);
    }

    @Transactional(readOnly = true)
    public Double getAverageNumberOfTeams() {
        return leagueRepository.averageNumberOfTeams();
    }

    @Transactional
    public void deleteLeague(UUID leagueId) {
        League league = leagueRepository.findById(leagueId)
                .orElseThrow(() -> new RuntimeException("League not found"));
        
        if (!league.getMatches().isEmpty()) {
            throw new RuntimeException("Cannot delete league with existing matches");
        }
        
        leagueRepository.delete(league);
    }
}