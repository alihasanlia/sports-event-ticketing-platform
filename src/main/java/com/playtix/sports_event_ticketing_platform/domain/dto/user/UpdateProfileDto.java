package com.playtix.sports_event_ticketing_platform.domain.dto.user;

import java.util.UUID;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateProfileDto(
    UUID id,
    
    @Size(min = 2, max = 50, message = "Firstname must be between 2 and 50 characters")
    String firstname,
    
    @Size(min = 2, max = 50, message = "Lastname must be between 2 and 50 characters")
    String lastname,
    
    @Pattern(regexp = "^09[0-9]{9}$", message = "Phone number must be a valid Iranian phone number")
    String phoneNumber,
    
    @Size(min = 2, max = 20, message = "City must be between 2 and 20 characters")
    String city
) {}