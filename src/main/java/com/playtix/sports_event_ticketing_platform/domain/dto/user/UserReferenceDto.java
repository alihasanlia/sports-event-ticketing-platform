package com.playtix.sports_event_ticketing_platform.domain.dto.user;

import java.util.UUID;

public record UserReferenceDto(
    UUID id,
    String firstname,
    String lastname
) {}
