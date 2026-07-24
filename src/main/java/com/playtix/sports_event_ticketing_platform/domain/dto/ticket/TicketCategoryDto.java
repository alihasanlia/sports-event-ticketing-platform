package com.playtix.sports_event_ticketing_platform.domain.dto.ticket;

import java.util.UUID;
import java.util.Locale.Category;

public record TicketCategoryDto(
    UUID id,
    Category category,
    String price,
    int remainingCapacity
) {}
