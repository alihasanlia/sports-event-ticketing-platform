package com.playtix.sports_event_ticketing_platform.domain.dto.reservation;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ConfirmReservationRequest(
    @NotNull(message = "Reservation ID is required")
    UUID reservationId
) {}