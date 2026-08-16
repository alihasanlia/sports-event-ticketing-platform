package com.playtix.sports_event_ticketing_platform.service;

import com.playtix.sports_event_ticketing_platform.domain.dto.payment.PaymentCalculationDto;
import com.playtix.sports_event_ticketing_platform.domain.dto.payment.CreatePaymentRequest;
import com.playtix.sports_event_ticketing_platform.domain.dto.payment.ProcessPaymentRequest;
import com.playtix.sports_event_ticketing_platform.domain.dto.payment.UserPaymentDto;
import com.playtix.sports_event_ticketing_platform.domain.entity.members.User;
import com.playtix.sports_event_ticketing_platform.domain.entity.payment.PaymentMethod;
import com.playtix.sports_event_ticketing_platform.domain.entity.reservation.Reservation;
import com.playtix.sports_event_ticketing_platform.repository.ReservationRepository;
import com.playtix.sports_event_ticketing_platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CheckoutService {

    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final PaymentService paymentService;

    @Transactional(readOnly = true)
    public PaymentCalculationDto calculatePayment(UUID userId, UUID reservationId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Reservation reservation = reservationRepository.findByIdWithTicket(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        if (!reservation.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Reservation does not belong to this user");
        }

        BigDecimal totalAmount = reservation.getTicket().getPrice();
        BigDecimal currentBalance = user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;
        
        BigDecimal walletPayment = BigDecimal.ZERO;
        BigDecimal remainingAmount = BigDecimal.ZERO;
        boolean fullyCovered = false;
        
        if (currentBalance.compareTo(totalAmount) >= 0) {
            walletPayment = totalAmount;
            remainingAmount = BigDecimal.ZERO;
            fullyCovered = true;
        } else {
            walletPayment = currentBalance;
            remainingAmount = totalAmount.subtract(currentBalance);
            fullyCovered = false;
        }

        String paymentMessage = fullyCovered ? 
            String.format("Full amount %s will be paid from wallet", totalAmount) :
            String.format("%s will be paid from wallet and remaining %s will be paid by card", 
                         walletPayment, remainingAmount);

        return new PaymentCalculationDto(
            totalAmount,
            currentBalance,
            walletPayment,
            remainingAmount,
            fullyCovered,
            paymentMessage
        );
    }

    @Transactional
    public UserPaymentDto processCheckout(UUID userId, UUID reservationId, String bankReceiptNumber) {
        PaymentCalculationDto calculation = calculatePayment(userId, reservationId);
        
        if (calculation.getRemainingAmount().compareTo(BigDecimal.ZERO) > 0 && 
            bankReceiptNumber == null) {
            throw new RuntimeException("Bank receipt number is required for remaining payment");
        }

        CreatePaymentRequest createRequest = new CreatePaymentRequest(
            userId,
            reservationId,
            calculation.getTotalAmount(),
            PaymentMethod.WALLET_AND_CARD
        );
        
        UserPaymentDto paymentDto = paymentService.createPayment(createRequest);
        
        ProcessPaymentRequest processRequest = new ProcessPaymentRequest(
            paymentDto.id(),
            bankReceiptNumber,
            calculation.getWalletPayment(),
            calculation.getRemainingAmount()
        );
        
        return paymentService.processPayment(processRequest);
    }
}