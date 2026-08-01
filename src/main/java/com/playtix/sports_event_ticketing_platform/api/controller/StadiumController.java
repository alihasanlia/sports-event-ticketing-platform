package com.playtix.sports_event_ticketing_platform.api.controller;

import com.playtix.sports_event_ticketing_platform.domain.dto.stadium.CreateStadiumRequest;
import com.playtix.sports_event_ticketing_platform.domain.dto.stadium.StadiumDto;
import com.playtix.sports_event_ticketing_platform.domain.dto.stadium.UpdateStadiumRequest;
import com.playtix.sports_event_ticketing_platform.service.StadiumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/stadiums")
@RequiredArgsConstructor
public class StadiumController {

    private final StadiumService stadiumService;

    @PostMapping
    public ResponseEntity<StadiumDto> createStadium(@Valid @RequestBody CreateStadiumRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(stadiumService.createStadium(request));
    }

    @PutMapping
    public ResponseEntity<StadiumDto> updateStadium(@Valid @RequestBody UpdateStadiumRequest request) {
        return ResponseEntity.ok(stadiumService.updateStadium(request));
    }

    @GetMapping("/{stadiumId}")
    public ResponseEntity<StadiumDto> getStadium(@PathVariable UUID stadiumId) {
        return ResponseEntity.ok(stadiumService.getStadium(stadiumId));
    }

    @GetMapping
    public ResponseEntity<List<StadiumDto>> getAllStadiums() {
        return ResponseEntity.ok(stadiumService.getAllStadiums());
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<List<StadiumDto>> getStadiumsByCity(@PathVariable String city) {
        return ResponseEntity.ok(stadiumService.getStadiumsByCity(city));
    }

    @GetMapping("/search")
    public ResponseEntity<List<StadiumDto>> searchStadiumsByName(@RequestParam String name) {
        return ResponseEntity.ok(stadiumService.searchStadiumsByName(name));
    }

    @GetMapping("/capacity-range")
    public ResponseEntity<List<StadiumDto>> getStadiumsByCapacityRange(
            @RequestParam int minCapacity,
            @RequestParam int maxCapacity) {
        return ResponseEntity.ok(stadiumService.getStadiumsByCapacityRange(minCapacity, maxCapacity));
    }

    @GetMapping("/sorted-by-name")
    public ResponseEntity<List<StadiumDto>> getStadiumsSortedByName() {
        return ResponseEntity.ok(stadiumService.getStadiumsSortedByName());
    }

    @GetMapping("/sorted-by-capacity")
    public ResponseEntity<List<StadiumDto>> getStadiumsSortedByCapacity() {
        return ResponseEntity.ok(stadiumService.getStadiumsSortedByCapacity());
    }

    @GetMapping("/largest")
    public ResponseEntity<List<StadiumDto>> getLargestStadiums() {
        return ResponseEntity.ok(stadiumService.getLargestStadiums());
    }

    @GetMapping("/smallest")
    public ResponseEntity<List<StadiumDto>> getSmallestStadiums() {
        return ResponseEntity.ok(stadiumService.getSmallestStadiums());
    }

    @GetMapping("/with-address")
    public ResponseEntity<List<StadiumDto>> getStadiumsWithAddress() {
        return ResponseEntity.ok(stadiumService.getStadiumsWithAddress());
    }

    @GetMapping("/city/{city}/count")
    public ResponseEntity<Long> countStadiumsByCity(@PathVariable String city) {
        return ResponseEntity.ok(stadiumService.countStadiumsByCity(city));
    }

    @GetMapping("/total-capacity")
    public ResponseEntity<Integer> getTotalCapacity() {
        return ResponseEntity.ok(stadiumService.getTotalCapacity());
    }

    @GetMapping("/city/{city}/total-capacity")
    public ResponseEntity<Integer> getTotalCapacityByCity(@PathVariable String city) {
        return ResponseEntity.ok(stadiumService.getTotalCapacityByCity(city));
    }

    @DeleteMapping("/{stadiumId}")
    public ResponseEntity<Void> deleteStadium(@PathVariable UUID stadiumId) {
        stadiumService.deleteStadium(stadiumId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
