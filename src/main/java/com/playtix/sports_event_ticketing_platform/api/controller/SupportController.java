package com.playtix.sports_event_ticketing_platform.api.controller;

import com.playtix.sports_event_ticketing_platform.domain.dto.user.SupportProfileDto;
import com.playtix.sports_event_ticketing_platform.domain.dto.user.SupportReferenceDto;
import com.playtix.sports_event_ticketing_platform.domain.dto.user.UpdateProfileDto;
import com.playtix.sports_event_ticketing_platform.domain.entity.members.AccountStatus;
import com.playtix.sports_event_ticketing_platform.service.SupportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/supports")
@RequiredArgsConstructor
public class SupportController {

    private final SupportService supportService;

    @GetMapping("/{supportId}/profile")
    public ResponseEntity<SupportProfileDto> getSupportProfile(@PathVariable UUID supportId) {
        return ResponseEntity.ok(supportService.getSupportProfile(supportId));
    }

    @GetMapping("/{supportId}/reference")
    public ResponseEntity<SupportReferenceDto> getSupportReference(@PathVariable UUID supportId) {
        return ResponseEntity.ok(supportService.getSupportReference(supportId));
    }

    @GetMapping
    public ResponseEntity<List<SupportProfileDto>> getAllSupports() {
        return ResponseEntity.ok(supportService.getAllSupportProfiles());
    }

    @GetMapping("/references")
    public ResponseEntity<List<SupportReferenceDto>> getAllSupportReferences() {
        return ResponseEntity.ok(supportService.getAllSupportReferences());
    }

    @PutMapping("/{supportId}/profile")
    public ResponseEntity<SupportProfileDto> updateSupportProfile(
            @PathVariable UUID supportId,
            @Valid @RequestBody UpdateProfileDto updateDto) {
        return ResponseEntity.ok(supportService.updateSupportProfile(supportId, updateDto));
    }

    @PatchMapping("/{supportId}/profile")
    public ResponseEntity<SupportProfileDto> updateSupportProfilePartial(
            @PathVariable UUID supportId,
            @RequestBody UpdateProfileDto updateDto) {
        return ResponseEntity.ok(supportService.updateSupportProfilePartial(supportId, updateDto));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<SupportProfileDto> getSupportByEmail(@PathVariable String email) {
        return ResponseEntity.ok(supportService.getSupportByEmail(email));
    }

    @GetMapping("/exists")
    public ResponseEntity<Boolean> checkSupportExists(@RequestParam String email) {
        return ResponseEntity.ok(supportService.existsByEmail(email));
    }

    @PostMapping("/{supportId}/deactivate")
    public ResponseEntity<Void> deactivateSupport(@PathVariable UUID supportId) {
        supportService.deactivateSupport(supportId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/{supportId}/activate")
    public ResponseEntity<Void> activateSupport(@PathVariable UUID supportId) {
        supportService.activateSupport(supportId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/active")
    public ResponseEntity<List<SupportReferenceDto>> getActiveSupports() {
        return ResponseEntity.ok(supportService.getActiveSupports());
    }

    @GetMapping("/available")
    public ResponseEntity<List<SupportReferenceDto>> getAvailableSupports() {
        return ResponseEntity.ok(supportService.getAvailableSupports());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<SupportReferenceDto>> getSupportsByStatus(@PathVariable AccountStatus status) {
        return ResponseEntity.ok(supportService.getSupportsByStatus(status));
    }

    @GetMapping("/search")
    public ResponseEntity<List<SupportReferenceDto>> searchSupports(
            @RequestParam(required = false) String firstname,
            @RequestParam(required = false) String lastname) {
        if (firstname != null && lastname != null) {
            return ResponseEntity.ok(supportService.searchSupportsByName(firstname, lastname));
        }
        return ResponseEntity.ok(supportService.getAllSupportReferences());
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<List<SupportReferenceDto>> getSupportsByCity(@PathVariable String city) {
        return ResponseEntity.ok(supportService.getSupportsByCity(city));
    }

    @GetMapping("/with-pending-reports")
    public ResponseEntity<List<SupportReferenceDto>> getSupportsWithPendingReports() {
        return ResponseEntity.ok(supportService.getSupportsWithPendingReports());
    }

    @GetMapping("/no-pending-reports")
    public ResponseEntity<List<SupportReferenceDto>> getSupportsWithNoPendingReports() {
        return ResponseEntity.ok(supportService.getSupportsWithNoPendingReports());
    }

    @GetMapping("/count/status/{status}")
    public ResponseEntity<Long> countSupportsByStatus(@PathVariable AccountStatus status) {
        return ResponseEntity.ok(supportService.countSupportsByStatus(status));
    }

    @GetMapping("/count/all")
    public ResponseEntity<Long> countAllSupports() {
        return ResponseEntity.ok(supportService.countAllSupports());
    }

    @GetMapping("/count/available")
    public ResponseEntity<Long> countAvailableSupports() {
        return ResponseEntity.ok(supportService.countAvailableSupports());
    }

    @GetMapping("/{supportId}/is-busy")
    public ResponseEntity<Boolean> isSupportBusy(@PathVariable UUID supportId) {
        return ResponseEntity.ok(supportService.isSupportBusy(supportId));
    }

    @GetMapping("/{supportId}/pending-reports-count")
    public ResponseEntity<Long> getPendingReportsCount(@PathVariable UUID supportId) {
        return ResponseEntity.ok(supportService.getPendingReportsCount(supportId));
    }

    @DeleteMapping("/{supportId}")
    public ResponseEntity<Void> deleteSupport(@PathVariable UUID supportId) {
        supportService.deleteSupport(supportId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}