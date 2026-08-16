package com.playtix.sports_event_ticketing_platform.domain.dto.team;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record UpdateTeamRequest(
    UUID id,
    
    @NotBlank(message = "Team name is required")
    String name,
    
    String city,
    
    String homeStadium,
    
    Integer foundedYear,
    
    String coach,
    
    String description,
    
    String logo
) {}