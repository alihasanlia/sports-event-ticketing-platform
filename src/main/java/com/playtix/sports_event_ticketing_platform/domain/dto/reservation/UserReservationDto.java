package com.playtix.sports_event_ticketing_platform.domain.dto.reservation;

import java.time.LocalDateTime;
import java.util.UUID;

import com.playtix.sports_event_ticketing_platform.domain.dto.ticket.TicketDetailsDto;
import com.playtix.sports_event_ticketing_platform.domain.entity.reservation.ReservationStatus;

public record UserReservationDto(
    UUID id,
    TicketDetailsDto ticketDetailsDto,
    ReservationStatus status,
    LocalDateTime reservationDate,
    LocalDateTime expiry,
    boolean isExpired,
    String timeRemaining
) {}
