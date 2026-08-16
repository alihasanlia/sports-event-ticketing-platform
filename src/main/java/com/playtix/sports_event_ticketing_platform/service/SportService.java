package com.playtix.sports_event_ticketing_platform.service;

import com.playtix.sports_event_ticketing_platform.domain.dto.sport.CreateSportRequest;
import com.playtix.sports_event_ticketing_platform.domain.dto.sport.SportDto;
import com.playtix.sports_event_ticketing_platform.domain.dto.sport.UpdateSportRequest;
import com.playtix.sports_event_ticketing_platform.domain.entity.Sport;
import com.playtix.sports_event_ticketing_platform.domain.entity.SportType;
import com.playtix.sports_event_ticketing_platform.repository.SportRepository;
import com.playtix.sports_event_ticketing_platform.mapper.SportMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SportService {

    private final SportRepository sportRepository;
    private final SportMapper sportMapper;

    @Transactional
    public SportDto createSport(CreateSportRequest request) {
        if (sportRepository.existsByName(request.name())) {
            throw new RuntimeException("Sport already exists with this name");
        }

        Sport sport = Sport.builder()
                .name(request.name())
                .description(request.description())
                .numberOfPlayers(request.numberOfPlayers())
                .build();

        sport = sportRepository.save(sport);
        return sportMapper.toSprotDto(sport);
    }

    @Transactional
    public SportDto updateSport(UpdateSportRequest request) {
        Sport sport = sportRepository.findById(request.id())
                .orElseThrow(() -> new RuntimeException("Sport not found"));

        sport.setDescription(request.description());
        sport.setNumberOfPlayers(request.numberOfPlayers());

        sport = sportRepository.save(sport);
        return sportMapper.toSprotDto(sport);
    }

    @Transactional(readOnly = true)
    public SportDto getSport(UUID sportId) {
        Sport sport = sportRepository.findById(sportId)
                .orElseThrow(() -> new RuntimeException("Sport not found"));
        return sportMapper.toSprotDto(sport);
    }

    @Transactional(readOnly = true)
    public SportDto getSportByName(SportType name) {
        Sport sport = sportRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Sport not found"));
        return sportMapper.toSprotDto(sport);
    }

    @Transactional(readOnly = true)
    public List<SportDto> getAllSports() {
        List<Sport> sports = sportRepository.findAll();
        return sportMapper.toSportDtoList(sports);
    }

    @Transactional(readOnly = true)
    public List<SportDto> getSportsSortedByName() {
        List<Sport> sports = sportRepository.findAllByOrderByNameAsc();
        return sportMapper.toSportDtoList(sports);
    }

    @Transactional(readOnly = true)
    public List<SportDto> getSportsWithLeagues() {
        List<Sport> sports = sportRepository.findSportsWithLeagues();
        return sportMapper.toSportDtoList(sports);
    }

    @Transactional(readOnly = true)
    public List<SportDto> getSportsWithTournaments() {
        List<Sport> sports = sportRepository.findSportsWithTournaments();
        return sportMapper.toSportDtoList(sports);
    }

    @Transactional(readOnly = true)
    public List<SportDto> getSportsWithoutLeagues() {
        List<Sport> sports = sportRepository.findSportsWithoutLeagues();
        return sportMapper.toSportDtoList(sports);
    }

    @Transactional(readOnly = true)
    public List<SportDto> getSportsWithoutTournaments() {
        List<Sport> sports = sportRepository.findSportsWithoutTournaments();
        return sportMapper.toSportDtoList(sports);
    }

    @Transactional(readOnly = true)
    public List<SportDto> getSportsWithBothLeaguesAndTournaments() {
        List<Sport> sports = sportRepository.findSportsWithBothLeaguesAndTournaments();
        return sportMapper.toSportDtoList(sports);
    }

    @Transactional(readOnly = true)
    public List<SportDto> getSportsWithLeaguesOrTournaments() {
        List<Sport> sports = sportRepository.findSportsWithLeaguesOrTournaments();
        return sportMapper.toSportDtoList(sports);
    }

    @Transactional(readOnly = true)
    public List<SportDto> getSportsSortedByPlayers() {
        List<Sport> sports = sportRepository.findAllOrderByNumberOfPlayersDesc();
        return sportMapper.toSportDtoList(sports);
    }

    @Transactional(readOnly = true)
    public List<SportDto> getSportsByPlayersRange(int minPlayers, int maxPlayers) {
        List<Sport> sports = sportRepository.findByNumberOfPlayersBetween(minPlayers, maxPlayers);
        return sportMapper.toSportDtoList(sports);
    }

    @Transactional(readOnly = true)
    public List<SportDto> searchSportsByDescription(String keyword) {
        List<Sport> sports = sportRepository.searchByDescriptionKeyword(keyword);
        return sportMapper.toSportDtoList(sports);
    }

    @Transactional(readOnly = true)
    public long countLeaguesBySport(UUID sportId) {
        return sportRepository.countLeaguesBySportId(sportId);
    }

    @Transactional(readOnly = true)
    public long countTournamentsBySport(UUID sportId) {
        return sportRepository.countTournamentsBySportId(sportId);
    }

    @Transactional(readOnly = true)
    public Double getAveragePlayers() {
        return sportRepository.averageNumberOfPlayers();
    }

    @Transactional(readOnly = true)
    public List<SportDto> getSportsAboveAveragePlayers() {
        List<Sport> sports = sportRepository.findSportsAboveAveragePlayers();
        return sportMapper.toSportDtoList(sports);
    }

    @Transactional(readOnly = true)
    public List<SportDto> getSportsBelowAveragePlayers() {
        List<Sport> sports = sportRepository.findSportsBelowAveragePlayers();
        return sportMapper.toSportDtoList(sports);
    }

    @Transactional
    public void deleteSport(UUID sportId) {
        Sport sport = sportRepository.findById(sportId)
                .orElseThrow(() -> new RuntimeException("Sport not found"));
        
        if (!sport.getLeagues().isEmpty() || !sport.getTournaments().isEmpty()) {
            throw new RuntimeException("Cannot delete sport with existing leagues or tournaments");
        }
        
        sportRepository.delete(sport);
    }
}