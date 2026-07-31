package com.playtix.sports_event_ticketing_platform.domain.dto.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.playtix.sports_event_ticketing_platform.domain.entity.payment.PaymentMethod;
import com.playtix.sports_event_ticketing_platform.domain.entity.payment.PaymentStatus;

public record UserPaymentDto(
    UUID id,
    BigDecimal amount,
    PaymentMethod paymentMethod,
    LocalDateTime paymentDate,
    PaymentStatus status,
    String transactionId,
    String bankReceiptNumber,
    String eventName
) {}
