package com.playtix.sports_event_ticketing_platform.service;

import com.playtix.sports_event_ticketing_platform.domain.dto.tournament.TournamentCreateRequestDTO;
import com.playtix.sports_event_ticketing_platform.domain.dto.tournament.TournamentResponseDTO;
import com.playtix.sports_event_ticketing_platform.domain.dto.tournament.TournamentStatusUpdateRequestDTO;
import com.playtix.sports_event_ticketing_platform.domain.dto.tournament.TournamentSummaryDTO;
import com.playtix.sports_event_ticketing_platform.domain.dto.tournament.TournamentUpdateRequestDTO;
import com.playtix.sports_event_ticketing_platform.domain.entity.Sport;
import com.playtix.sports_event_ticketing_platform.domain.entity.tournament.Tournament;
import com.playtix.sports_event_ticketing_platform.domain.entity.tournament.TournamentStatus;
import com.playtix.sports_event_ticketing_platform.repository.SportRepository;
import com.playtix.sports_event_ticketing_platform.repository.TournamentRepository;
import com.playtix.sports_event_ticketing_platform.mapper.TournamentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TournamentService {

    private final TournamentRepository tournamentRepository;
    private final SportRepository sportRepository;
    private final TournamentMapper tournamentMapper;

    @Transactional
    public TournamentResponseDTO createTournament(TournamentCreateRequestDTO request) {
        if (tournamentRepository.existsByNameIgnoreCase(request.name())) {
            throw new RuntimeException("Tournament already exists with this name");
        }

        Sport sport = sportRepository.findById(request.sportId())
                .orElseThrow(() -> new RuntimeException("Sport not found"));

        Tournament tournament = Tournament.builder()
                .name(request.name())
                .description(request.description())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .status(TournamentStatus.UPCOMING)
                .sport(sport)
                .build();

        tournament = tournamentRepository.save(tournament);
        return tournamentMapper.toResponseDTO(tournament);
    }

    @Transactional
    public TournamentResponseDTO updateTournament(TournamentUpdateRequestDTO request) {
        Tournament tournament = tournamentRepository.findById(request.id())
                .orElseThrow(() -> new RuntimeException("Tournament not found"));

        if (!tournament.getName().equals(request.name()) && 
            tournamentRepository.existsByNameIgnoreCase(request.name())) {
            throw new RuntimeException("Tournament already exists with this name");
        }

        tournament.setName(request.name());
        tournament.setDescription(request.description());
        tournament.setStartDate(request.startDate());
        tournament.setEndDate(request.endDate());

        if (request.sportId() != null) {
            Sport sport = sportRepository.findById(request.sportId())
                    .orElseThrow(() -> new RuntimeException("Sport not found"));
            tournament.setSport(sport);
        }

        tournament = tournamentRepository.save(tournament);
        return tournamentMapper.toResponseDTO(tournament);
    }

    @Transactional
    public TournamentResponseDTO updateStatus(TournamentStatusUpdateRequestDTO request, UUID tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));

        TournamentStatus newStatus = request.status();
        TournamentStatus currentStatus = tournament.getStatus();

        if (newStatus == currentStatus) {
            throw new RuntimeException("Tournament is already in " + newStatus + " status");
        }

        switch (newStatus) {
            case ACTIVE:
                tournament.start();
                break;
            case FINISHED:
                tournament.finish();
                break;
            case CANCELLED:
                tournament.cancel();
                break;
            case UPCOMING:
                throw new RuntimeException("Cannot manually set status to UPCOMING");
            default:
                throw new RuntimeException("Invalid status transition");
        }

        tournament = tournamentRepository.save(tournament);
        return tournamentMapper.toResponseDTO(tournament);
    }

    @Transactional
    public TournamentResponseDTO startTournament(UUID tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));
        tournament.start();
        tournament = tournamentRepository.save(tournament);
        return tournamentMapper.toResponseDTO(tournament);
    }

    @Transactional
    public TournamentResponseDTO finishTournament(UUID tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));
        tournament.finish();
        tournament = tournamentRepository.save(tournament);
        return tournamentMapper.toResponseDTO(tournament);
    }

    @Transactional
    public TournamentResponseDTO cancelTournament(UUID tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));
        tournament.cancel();
        tournament = tournamentRepository.save(tournament);
        return tournamentMapper.toResponseDTO(tournament);
    }

    @Transactional(readOnly = true)
    public TournamentResponseDTO getTournament(UUID tournamentId) {
        Tournament tournament = tournamentRepository.findByIdWithMatchesAndSport(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));
        return tournamentMapper.toResponseDTO(tournament);
    }

    @Transactional(readOnly = true)
    public TournamentSummaryDTO getTournamentSummary(UUID tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));
        return tournamentMapper.toSummaryDTO(tournament);
    }

    @Transactional(readOnly = true)
    public List<TournamentResponseDTO> getAllTournaments() {
        List<Tournament> tournaments = tournamentRepository.findAll();
        return tournamentMapper.toResponseDTOList(tournaments);
    }

    @Transactional(readOnly = true)
    public List<TournamentSummaryDTO> getAllTournamentSummaries() {
        List<Tournament> tournaments = tournamentRepository.findAll();
        return tournamentMapper.toSummaryDTOList(tournaments);
    }

    @Transactional(readOnly = true)
    public List<TournamentResponseDTO> getTournamentsBySport(UUID sportId) {
        List<Tournament> tournaments = tournamentRepository.findBySport_Id(sportId);
        return tournamentMapper.toResponseDTOList(tournaments);
    }

    @Transactional(readOnly = true)
    public List<TournamentResponseDTO> getTournamentsByStatus(TournamentStatus status) {
        List<Tournament> tournaments = tournamentRepository.findByStatus(status);
        return tournamentMapper.toResponseDTOList(tournaments);
    }

    @Transactional(readOnly = true)
    public List<TournamentResponseDTO> getUpcomingTournaments() {
        List<Tournament> tournaments = tournamentRepository.findUpcomingTournaments(LocalDateTime.now());
        return tournamentMapper.toResponseDTOList(tournaments);
    }

    @Transactional(readOnly = true)
    public List<TournamentResponseDTO> getActiveTournaments() {
        List<Tournament> tournaments = tournamentRepository.findActiveTournaments(LocalDateTime.now());
        return tournamentMapper.toResponseDTOList(tournaments);
    }

    @Transactional(readOnly = true)
    public List<TournamentResponseDTO> getFinishedTournaments() {
        List<Tournament> tournaments = tournamentRepository.findFinishedTournaments(LocalDateTime.now());
        return tournamentMapper.toResponseDTOList(tournaments);
    }

    @Transactional(readOnly = true)
    public List<TournamentResponseDTO> getCancelledTournaments() {
        List<Tournament> tournaments = tournamentRepository.findCancelledTournaments();
        return tournamentMapper.toResponseDTOList(tournaments);
    }

    @Transactional(readOnly = true)
    public List<TournamentResponseDTO> getTournamentsWithMatches() {
        List<Tournament> tournaments = tournamentRepository.findTournamentsWithoutMatches();
        return tournamentMapper.toResponseDTOList(tournaments);
    }

    @Transactional(readOnly = true)
    public List<TournamentResponseDTO> getTournamentsWithoutMatches() {
        List<Tournament> tournaments = tournamentRepository.findTournamentsWithoutMatches();
        return tournamentMapper.toResponseDTOList(tournaments);
    }

    @Transactional(readOnly = true)
    public List<TournamentResponseDTO> searchTournamentsByName(String name) {
        List<Tournament> tournaments = tournamentRepository.findByNameContainingIgnoreCase(name);
        return tournamentMapper.toResponseDTOList(tournaments);
    }

    @Transactional(readOnly = true)
    public List<TournamentResponseDTO> getTournamentsSortedByName() {
        List<Tournament> tournaments = tournamentRepository.findAllByOrderByNameAsc();
        return tournamentMapper.toResponseDTOList(tournaments);
    }

    @Transactional(readOnly = true)
    public List<TournamentResponseDTO> getTournamentsSortedByDate() {
        List<Tournament> tournaments = tournamentRepository.findAllByOrderByStartDateDesc();
        return tournamentMapper.toResponseDTOList(tournaments);
    }

    @Transactional(readOnly = true)
    public List<TournamentResponseDTO> getLatestTournaments() {
        List<Tournament> tournaments = tournamentRepository.findLatestTournaments();
        return tournamentMapper.toResponseDTOList(tournaments);
    }

    @Transactional(readOnly = true)
    public long countMatchesByTournament(UUID tournamentId) {
        return tournamentRepository.countMatchesByTournamentId(tournamentId);
    }

    @Transactional(readOnly = true)
    public long countUpcomingMatchesByTournament(UUID tournamentId) {
        return tournamentRepository.countUpcomingMatchesByTournamentId(tournamentId, LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public long countFinishedMatchesByTournament(UUID tournamentId) {
        return tournamentRepository.countFinishedMatchesByTournamentId(tournamentId, LocalDateTime.now());
    }

    @Transactional
    public void deleteTournament(UUID tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));
        
        if (!tournament.getMatches().isEmpty()) {
            throw new RuntimeException("Cannot delete tournament with existing matches");
        }
        
        tournamentRepository.delete(tournament);
    }
}