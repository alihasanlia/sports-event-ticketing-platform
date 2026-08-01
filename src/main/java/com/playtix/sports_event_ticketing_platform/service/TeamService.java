package com.playtix.sports_event_ticketing_platform.service;

import com.playtix.sports_event_ticketing_platform.domain.dto.team.CreateTeamRequest;
import com.playtix.sports_event_ticketing_platform.domain.dto.team.TeamDto;
import com.playtix.sports_event_ticketing_platform.domain.dto.team.UpdateTeamRequest;
import com.playtix.sports_event_ticketing_platform.domain.entity.Team;
import com.playtix.sports_event_ticketing_platform.repository.TeamRepository;
import com.playtix.sports_event_ticketing_platform.mapper.TeamMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMapper teamMapper;

    @Transactional
    public TeamDto createTeam(CreateTeamRequest request) {
        if (teamRepository.existsByNameIgnoreCase(request.name())) {
            throw new RuntimeException("Team already exists with this name");
        }

        Team team = Team.builder()
                .name(request.name())
                .city(request.city())
                .homeStadium(request.homeStadium())
                .foundedYear(request.foundedYear())
                .coach(request.coach())
                .description(request.description())
                .logo(request.logo())
                .build();

        team = teamRepository.save(team);
        return teamMapper.toTeamDto(team);
    }

    @Transactional
    public TeamDto updateTeam(UpdateTeamRequest request) {
        Team team = teamRepository.findById(request.id())
                .orElseThrow(() -> new RuntimeException("Team not found"));

        if (!team.getName().equals(request.name()) && 
            teamRepository.existsByNameIgnoreCase(request.name())) {
            throw new RuntimeException("Team already exists with this name");
        }

        team.setName(request.name());
        team.setCity(request.city());
        team.setHomeStadium(request.homeStadium());
        team.setFoundedYear(request.foundedYear());
        team.setCoach(request.coach());
        team.setDescription(request.description());
        team.setLogo(request.logo());

        team = teamRepository.save(team);
        return teamMapper.toTeamDto(team);
    }

    @Transactional(readOnly = true)
    public TeamDto getTeam(UUID teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));
        return teamMapper.toTeamDto(team);
    }

    @Transactional(readOnly = true)
    public List<TeamDto> getAllTeams() {
        List<Team> teams = teamRepository.findAll();
        return teamMapper.toTeamDtoList(teams);
    }

    @Transactional(readOnly = true)
    public List<TeamDto> searchTeamsByName(String name) {
        List<Team> teams = teamRepository.findByNameContainingIgnoreCase(name);
        return teamMapper.toTeamDtoList(teams);
    }

    @Transactional(readOnly = true)
    public List<TeamDto> getTeamsByCity(String city) {
        List<Team> teams = teamRepository.findByCity(city);
        return teamMapper.toTeamDtoList(teams);
    }

    @Transactional(readOnly = true)
    public List<TeamDto> getTeamsByCoach(String coach) {
        List<Team> teams = teamRepository.findByCoachContainingIgnoreCase(coach);
        return teamMapper.toTeamDtoList(teams);
    }

    @Transactional(readOnly = true)
    public List<TeamDto> getTeamsByFoundedYearRange(int startYear, int endYear) {
        List<Team> teams = teamRepository.findByFoundedYearBetween(startYear, endYear);
        return teamMapper.toTeamDtoList(teams);
    }

    @Transactional(readOnly = true)
    public List<TeamDto> getTeamsSortedByName() {
        List<Team> teams = teamRepository.findAllByOrderByNameAsc();
        return teamMapper.toTeamDtoList(teams);
    }

    @Transactional(readOnly = true)
    public List<TeamDto> getTeamsSortedByFoundedYear() {
        List<Team> teams = teamRepository.findAllByOrderByFoundedYearDesc();
        return teamMapper.toTeamDtoList(teams);
    }

    @Transactional(readOnly = true)
    public List<TeamDto> getTeamsWithMatches() {
        List<Team> teams = teamRepository.findTeamsWithAnyMatch();
        return teamMapper.toTeamDtoList(teams);
    }

    @Transactional(readOnly = true)
    public List<TeamDto> getTeamsWithoutMatches() {
        List<Team> teams = teamRepository.findTeamsWithoutMatches();
        return teamMapper.toTeamDtoList(teams);
    }

    @Transactional(readOnly = true)
    public List<TeamDto> getTeamsWithUpcomingMatches() {
        List<Team> teams = teamRepository.findTeamsWithUpcomingMatches(java.time.LocalDateTime.now());
        return teamMapper.toTeamDtoList(teams);
    }

    @Transactional(readOnly = true)
    public long countAllMatchesByTeam(UUID teamId) {
        return teamRepository.countAllHomeMatchesByTeamId(teamId) + 
               teamRepository.countAllAwayMatchesByTeamId(teamId);
    }

    @Transactional(readOnly = true)
    public long countUpcomingMatchesByTeam(UUID teamId) {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        return teamRepository.countUpcomingHomeMatchesByTeamId(teamId, now) + 
               teamRepository.countUpcomingAwayMatchesByTeamId(teamId, now);
    }

    @Transactional(readOnly = true)
    public Double getAverageFoundedYear() {
        return teamRepository.averageFoundedYear();
    }

    @Transactional(readOnly = true)
    public Integer getLatestFoundedYear() {
        return teamRepository.findLatestFoundedYear();
    }

    @Transactional(readOnly = true)
    public Integer getEarliestFoundedYear() {
        return teamRepository.findEarliestFoundedYear();
    }

    @Transactional
    public void deleteTeam(UUID teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));
        
        if (!team.getHomeMatches().isEmpty() || !team.getAwayMatches().isEmpty()) {
            throw new RuntimeException("Cannot delete team with existing matches");
        }
        
        teamRepository.delete(team);
    }
}