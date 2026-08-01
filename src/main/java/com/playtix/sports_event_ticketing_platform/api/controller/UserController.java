package com.playtix.sports_event_ticketing_platform.api.controller;

import com.playtix.sports_event_ticketing_platform.domain.dto.user.UpdateProfileDto;
import com.playtix.sports_event_ticketing_platform.domain.dto.user.UserProfileDto;
import com.playtix.sports_event_ticketing_platform.domain.dto.user.UserReferenceDto;
import com.playtix.sports_event_ticketing_platform.domain.entity.members.AccountStatus;
import com.playtix.sports_event_ticketing_platform.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}/profile")
    public ResponseEntity<UserProfileDto> getUserProfile(@PathVariable UUID userId) {
        return ResponseEntity.ok(userService.getUserProfile(userId));
    }

    @GetMapping("/{userId}/reference")
    public ResponseEntity<UserReferenceDto> getUserReference(@PathVariable UUID userId) {
        return ResponseEntity.ok(userService.getUserReference(userId));
    }

    @GetMapping
    public ResponseEntity<List<UserProfileDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUserProfiles());
    }

    @GetMapping("/references")
    public ResponseEntity<List<UserReferenceDto>> getAllUserReferences() {
        return ResponseEntity.ok(userService.getAllUserReferences());
    }

    @PutMapping("/{userId}/profile")
    public ResponseEntity<UserProfileDto> updateUserProfile(
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateProfileDto updateDto) {
        return ResponseEntity.ok(userService.updateUserProfile(userId, updateDto));
    }

    @PatchMapping("/{userId}/profile")
    public ResponseEntity<UserProfileDto> updateUserProfilePartial(
            @PathVariable UUID userId,
            @RequestBody UpdateProfileDto updateDto) {
        return ResponseEntity.ok(userService.updateUserProfilePartial(userId, updateDto));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserProfileDto> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @GetMapping("/exists")
    public ResponseEntity<Boolean> checkUserExists(@RequestParam String email) {
        return ResponseEntity.ok(userService.existsByEmail(email));
    }

    @PostMapping("/{userId}/deactivate")
    public ResponseEntity<Void> deactivateUser(@PathVariable UUID userId) {
        userService.deactivateUser(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/{userId}/activate")
    public ResponseEntity<Void> activateUser(@PathVariable UUID userId) {
        userService.activateUser(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/active")
    public ResponseEntity<List<UserReferenceDto>> getActiveUsers() {
        return ResponseEntity.ok(userService.getActiveUsers());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<UserReferenceDto>> getUsersByStatus(@PathVariable AccountStatus status) {
        return ResponseEntity.ok(userService.getUsersByStatus(status));
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserReferenceDto>> searchUsers(
            @RequestParam(required = false) String firstname,
            @RequestParam(required = false) String lastname) {
        if (firstname != null && lastname != null) {
            return ResponseEntity.ok(userService.searchUsersByName(firstname, lastname));
        }
        return ResponseEntity.ok(userService.getAllUserReferences());
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<List<UserReferenceDto>> getUsersByCity(@PathVariable String city) {
        return ResponseEntity.ok(userService.getUsersByCity(city));
    }

    @GetMapping("/count/status/{status}")
    public ResponseEntity<Long> countUsersByStatus(@PathVariable AccountStatus status) {
        return ResponseEntity.ok(userService.countUsersByStatus(status));
    }

    @GetMapping("/count/all")
    public ResponseEntity<Long> countAllUsers() {
        return ResponseEntity.ok(userService.countAllUsers());
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
        userService.deleteUser(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}