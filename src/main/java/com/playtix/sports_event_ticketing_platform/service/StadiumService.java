package com.playtix.sports_event_ticketing_platform.service;

import com.playtix.sports_event_ticketing_platform.domain.dto.stadium.CreateStadiumRequest;
import com.playtix.sports_event_ticketing_platform.domain.dto.stadium.StadiumDto;
import com.playtix.sports_event_ticketing_platform.domain.dto.stadium.UpdateStadiumRequest;
import com.playtix.sports_event_ticketing_platform.domain.entity.Stadium;
import com.playtix.sports_event_ticketing_platform.repository.StadiumRepository;
import com.playtix.sports_event_ticketing_platform.mapper.StadiumMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StadiumService {

    private final StadiumRepository stadiumRepository;
    private final StadiumMapper stadiumMapper;

    @Transactional
    public StadiumDto createStadium(CreateStadiumRequest request) {
        if (stadiumRepository.existsByNameIgnoreCase(request.name())) {
            throw new RuntimeException("Stadium already exists with this name");
        }

        Stadium stadium = Stadium.builder()
                .name(request.name())
                .city(request.city())
                .capacity(request.capacity())
                .address(request.address())
                .build();

        stadium = stadiumRepository.save(stadium);
        return stadiumMapper.toStadiumDto(stadium);
    }

    @Transactional
    public StadiumDto updateStadium(UpdateStadiumRequest request) {
        Stadium stadium = stadiumRepository.findById(request.id())
                .orElseThrow(() -> new RuntimeException("Stadium not found"));

        if (!stadium.getName().equals(request.name()) && 
            stadiumRepository.existsByNameIgnoreCase(request.name())) {
            throw new RuntimeException("Stadium already exists with this name");
        }

        stadium.setName(request.name());
        stadium.setCapacity(request.capacity());
        stadium.setAddress(request.address());

        stadium = stadiumRepository.save(stadium);
        return stadiumMapper.toStadiumDto(stadium);
    }

    @Transactional(readOnly = true)
    public StadiumDto getStadium(UUID stadiumId) {
        Stadium stadium = stadiumRepository.findById(stadiumId)
                .orElseThrow(() -> new RuntimeException("Stadium not found"));
        return stadiumMapper.toStadiumDto(stadium);
    }

    @Transactional(readOnly = true)
    public List<StadiumDto> getAllStadiums() {
        List<Stadium> stadiums = stadiumRepository.findAll();
        return stadiumMapper.toStadiumDtoList(stadiums);
    }

    @Transactional(readOnly = true)
    public List<StadiumDto> getStadiumsByCity(String city) {
        List<Stadium> stadiums = stadiumRepository.findByCity(city);
        return stadiumMapper.toStadiumDtoList(stadiums);
    }

    @Transactional(readOnly = true)
    public List<StadiumDto> searchStadiumsByName(String name) {
        List<Stadium> stadiums = stadiumRepository.findByNameContainingIgnoreCase(name);
        return stadiumMapper.toStadiumDtoList(stadiums);
    }

    @Transactional(readOnly = true)
    public List<StadiumDto> getStadiumsByCapacityRange(int minCapacity, int maxCapacity) {
        List<Stadium> stadiums = stadiumRepository.findByCapacityBetween(minCapacity, maxCapacity);
        return stadiumMapper.toStadiumDtoList(stadiums);
    }

    @Transactional(readOnly = true)
    public List<StadiumDto> getStadiumsSortedByName() {
        List<Stadium> stadiums = stadiumRepository.findAllByOrderByNameAsc();
        return stadiumMapper.toStadiumDtoList(stadiums);
    }

    @Transactional(readOnly = true)
    public List<StadiumDto> getStadiumsSortedByCapacity() {
        List<Stadium> stadiums = stadiumRepository.findAllByOrderByCapacityDesc();
        return stadiumMapper.toStadiumDtoList(stadiums);
    }

    @Transactional(readOnly = true)
    public List<StadiumDto> getLargestStadiums() {
        List<Stadium> stadiums = stadiumRepository.findLargestStadiums();
        return stadiumMapper.toStadiumDtoList(stadiums);
    }

    @Transactional(readOnly = true)
    public List<StadiumDto> getSmallestStadiums() {
        List<Stadium> stadiums = stadiumRepository.findSmallestStadiums();
        return stadiumMapper.toStadiumDtoList(stadiums);
    }

    @Transactional(readOnly = true)
    public List<StadiumDto> getStadiumsWithAddress() {
        List<Stadium> stadiums = stadiumRepository.findStadiumsWithAddress();
        return stadiumMapper.toStadiumDtoList(stadiums);
    }

    @Transactional(readOnly = true)
    public long countStadiumsByCity(String city) {
        return stadiumRepository.countByCity(city);
    }

    @Transactional(readOnly = true)
    public int getTotalCapacity() {
        Integer total = stadiumRepository.sumTotalCapacity();
        return total != null ? total : 0;
    }

    @Transactional(readOnly = true)
    public int getTotalCapacityByCity(String city) {
        Integer total = stadiumRepository.sumCapacityByCity(city);
        return total != null ? total : 0;
    }

    @Transactional
    public void deleteStadium(UUID stadiumId) {
        Stadium stadium = stadiumRepository.findById(stadiumId)
                .orElseThrow(() -> new RuntimeException("Stadium not found"));
        
        if (stadium.getMatches() != null && !stadium.getMatches().isEmpty()) {
            throw new RuntimeException("Cannot delete stadium with existing matches");
        }
        
        stadiumRepository.delete(stadium);
    }
}