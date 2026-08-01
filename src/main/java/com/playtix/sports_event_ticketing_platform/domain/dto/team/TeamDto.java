package com.playtix.sports_event_ticketing_platform.domain.dto.team;

import java.util.UUID;

public record TeamDto(
    UUID id,
    String name,
    String city,
    String homeStadium,
    Integer foundedYear,
    String coach,
    String description,
    String logo
) {}
