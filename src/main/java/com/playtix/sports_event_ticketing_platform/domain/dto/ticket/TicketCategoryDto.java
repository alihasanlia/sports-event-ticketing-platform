package com.playtix.sports_event_ticketing_platform.domain.dto.ticket;

import java.math.BigDecimal;
import java.util.UUID;

import com.playtix.sports_event_ticketing_platform.domain.entity.ticket.Category;

public record TicketCategoryDto(
    UUID id,
    Category category,
    BigDecimal price,
    int remainingCapacity
) {}
