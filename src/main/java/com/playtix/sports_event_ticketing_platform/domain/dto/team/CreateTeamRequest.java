package com.playtix.sports_event_ticketing_platform.domain.dto.team;

import jakarta.validation.constraints.NotBlank;

public record CreateTeamRequest(
    @NotBlank(message = "Team name is required")
    String name,
    
    String city,
    
    String homeStadium,
    
    Integer foundedYear,
    
    String coach,
    
    String description,
    
    String logo
) {}