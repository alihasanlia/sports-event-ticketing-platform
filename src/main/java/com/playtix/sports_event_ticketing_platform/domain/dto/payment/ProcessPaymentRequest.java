package com.playtix.sports_event_ticketing_platform.domain.dto.payment;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ProcessPaymentRequest(
    @NotNull(message = "Payment ID is required")
    UUID paymentId,
    
    String bankReceiptNumber
) {}