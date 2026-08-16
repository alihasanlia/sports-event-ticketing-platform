package com.playtix.sports_event_ticketing_platform.domain.dto.payment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import com.playtix.sports_event_ticketing_platform.domain.entity.payment.PaymentMethod;

import java.math.BigDecimal;
import java.util.UUID;

public record CreatePaymentRequest(
    @NotNull(message = "User ID is required")
    UUID userId,
    
    @NotNull(message = "Reservation ID is required")
    UUID reservationId,
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    BigDecimal amount,
    
    @NotNull(message = "Payment method is required")
    PaymentMethod paymentMethod
) {}