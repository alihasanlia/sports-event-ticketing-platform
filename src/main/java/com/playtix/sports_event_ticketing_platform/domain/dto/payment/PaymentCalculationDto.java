package com.playtix.sports_event_ticketing_platform.domain.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class PaymentCalculationDto {
    private BigDecimal totalAmount;
    private BigDecimal currentBalance;
    private BigDecimal walletPayment;
    private BigDecimal remainingAmount;
    private boolean fullyCovered;
    private String paymentMessage;
}