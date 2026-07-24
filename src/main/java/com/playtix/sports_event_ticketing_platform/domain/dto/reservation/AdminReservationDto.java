package com.playtix.sports_event_ticketing_platform.domain.dto.reservation;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.playtix.sports_event_ticketing_platform.domain.dto.user.UserReferenceDto;
import com.playtix.sports_event_ticketing_platform.domain.entity.reservation.ReservationStatus;

public record AdminReservationDto(
    UUID id,
    UserReferenceDto user,
    String eventName,
    String venueName,
    LocalDateTime eventDateTime,
    String seatNumber,
    String section,
    ReservationStatus status,
    LocalDateTime reservationDate,
    int quantity,
    BigDecimal totalPrice,
    LocalDateTime expiry,
    boolean isExpired,
    String timeRemaining
) {}
