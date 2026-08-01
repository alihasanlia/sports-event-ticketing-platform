package com.playtix.sports_event_ticketing_platform.service.cancellation;

import com.playtix.sports_event_ticketing_platform.domain.dto.cancellation.CancellationDto;
import com.playtix.sports_event_ticketing_platform.domain.dto.cancellation.CreateCancellationRequestDto;
import com.playtix.sports_event_ticketing_platform.domain.entity.TicketCancellation;
import com.playtix.sports_event_ticketing_platform.domain.entity.members.User;
import com.playtix.sports_event_ticketing_platform.domain.entity.ticket.Ticket;
import com.playtix.sports_event_ticketing_platform.domain.entity.ticket.TicketStatus;
import com.playtix.sports_event_ticketing_platform.mapper.CancellationMapper;
import com.playtix.sports_event_ticketing_platform.repository.TicketCancellationRepository;
import com.playtix.sports_event_ticketing_platform.repository.TicketRepository;
import com.playtix.sports_event_ticketing_platform.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CancellationService {

    private final TicketCancellationRepository cancellationRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final CancellationMapper cancellationMapper;

    private static final int FULL_REFUND_DAYS = 3;
    private static final int PARTIAL_REFUND_DAYS = 1;
    private static final int FULL_REFUND_PENALTY = 0;
    private static final int PARTIAL_REFUND_PENALTY = 20;
    private static final int LATE_REFUND_PENALTY = 50;

    @Transactional
    public CancellationDto createCancellation(CreateCancellationRequestDto request) {
        log.info("Creating cancellation for ticket: {} by user: {}", request.ticketId(), request.userId());

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + request.userId()));

        Ticket ticket = ticketRepository.findByIdWithMatchAndCategory(request.ticketId())
                .orElseThrow(() -> new RuntimeException("Ticket not found with id: " + request.ticketId()));

        validateCancellationRequest(ticket);
        
        int penaltyPercent = calculatePenaltyPercent(ticket);
        log.info("Calculated penalty percent: {}% for ticket: {}", penaltyPercent, ticket.getId());

        BigDecimal refundAmount = calculateRefundAmount(ticket, penaltyPercent);
        BigDecimal cancellationFee = calculateCancellationFee(ticket, penaltyPercent);

        TicketCancellation cancellation = TicketCancellation.builder()
                .user(user)
                .ticket(ticket)
                .penaltyPercent(penaltyPercent)
                .refundAmount(refundAmount)
                .cancellationFee(cancellationFee)
                .reason(request.reason())
                .build();

        cancellation = cancellationRepository.save(cancellation);

        ticket.setStatus(TicketStatus.NOT_RESERVED);
        ticketRepository.save(ticket);

        ticket.getTicketCategory().cancelReservation();

        log.info("Cancellation created successfully with id: {}, refund amount: {}, penalty: {}%", 
                cancellation.getId(), refundAmount, penaltyPercent);

        return cancellationMapper.toCancellationDto(cancellation);
    }

    private int calculatePenaltyPercent(Ticket ticket) {
        LocalDateTime matchDateTime = ticket.getMatch().getMatchDate();
        if (matchDateTime == null) {
            log.warn("Match date is null for ticket: {}, applying default penalty", ticket.getId());
            return PARTIAL_REFUND_PENALTY;
        }

        LocalDateTime now = LocalDateTime.now();
        
        if (now.isAfter(matchDateTime)) {
            throw new RuntimeException("Cannot cancel a ticket for a match that has already passed");
        }

        long daysUntilMatch = ChronoUnit.DAYS.between(now, matchDateTime);
        long hoursUntilMatch = ChronoUnit.HOURS.between(now, matchDateTime);

        log.debug("Days until match: {}, Hours until match: {}", daysUntilMatch, hoursUntilMatch);

        if (daysUntilMatch > FULL_REFUND_DAYS) {
            return FULL_REFUND_PENALTY;
        } else if (daysUntilMatch >= PARTIAL_REFUND_DAYS || hoursUntilMatch >= 24) {
            return PARTIAL_REFUND_PENALTY;
        } else {
            return LATE_REFUND_PENALTY;
        }
    }

    @Transactional(readOnly = true)
    public int calculatePenaltyPercentForTicket(UUID ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found with id: " + ticketId));
        return calculatePenaltyPercent(ticket);
    }

    @Transactional(readOnly = true)
    public BigDecimal calculateRefundAmountForTicket(UUID ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found with id: " + ticketId));
        int penaltyPercent = calculatePenaltyPercent(ticket);
        return calculateRefundAmount(ticket, penaltyPercent);
    }

    @Transactional(readOnly = true)
    public CancellationEligibility checkCancellationEligibility(UUID ticketId) {
        Ticket ticket = ticketRepository.findByIdWithMatchAndCategory(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found with id: " + ticketId));

        if (ticket.getStatus() != TicketStatus.SOLD) {
            return CancellationEligibility.builder()
                    .eligible(false)
                    .reason("Ticket is not sold. Current status: " + ticket.getStatus())
                    .build();
        }

        if (cancellationRepository.existsByTicket_Id(ticketId)) {
            return CancellationEligibility.builder()
                    .eligible(false)
                    .reason("Ticket already has a cancellation request")
                    .build();
        }

        LocalDateTime matchDateTime = ticket.getMatch().getMatchDate();
        if (matchDateTime == null) {
            return CancellationEligibility.builder()
                    .eligible(false)
                    .reason("Match date is not set")
                    .build();
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(matchDateTime)) {
            return CancellationEligibility.builder()
                    .eligible(false)
                    .reason("Match has already passed")
                    .build();
        }

        int penaltyPercent = calculatePenaltyPercent(ticket);
        BigDecimal refundAmount = calculateRefundAmount(ticket, penaltyPercent);
        long daysUntilMatch = ChronoUnit.DAYS.between(now, matchDateTime);
        long hoursUntilMatch = ChronoUnit.HOURS.between(now, matchDateTime);

        return CancellationEligibility.builder()
                .eligible(true)
                .penaltyPercent(penaltyPercent)
                .refundAmount(refundAmount)
                .originalPrice(ticket.getFinalPrice())
                .daysUntilMatch(daysUntilMatch)
                .hoursUntilMatch(hoursUntilMatch)
                .matchDateTime(matchDateTime)
                .refundPercentage(100 - penaltyPercent)
                .build();
    }

    @Transactional(readOnly = true)
    public CancellationDto getCancellationById(UUID cancellationId) {
        log.debug("Fetching cancellation by id: {}", cancellationId);
        
        TicketCancellation cancellation = cancellationRepository.findById(cancellationId)
                .orElseThrow(() -> new RuntimeException("Cancellation not found with id: " + cancellationId));
        
        return cancellationMapper.toCancellationDto(cancellation);
    }

    @Transactional(readOnly = true)
    public List<CancellationDto> getUserCancellations(UUID userId) {
        log.debug("Fetching cancellations for user: {}", userId);
        
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with id: " + userId);
        }
        
        List<TicketCancellation> cancellations = cancellationRepository.findByUser_IdOrderByRequestDateDesc(userId);
        return cancellationMapper.toCancellationDtoList(cancellations);
    }

    @Transactional(readOnly = true)
    public List<CancellationDto> getTicketCancellations(UUID ticketId) {
        log.debug("Fetching cancellations for ticket: {}", ticketId);
        
        if (!ticketRepository.existsById(ticketId)) {
            throw new RuntimeException("Ticket not found with id: " + ticketId);
        }
        
        List<TicketCancellation> cancellations = cancellationRepository.findByTicketIdOrderByRequestDateDesc(ticketId);
        return cancellationMapper.toCancellationDtoList(cancellations);
    }

    @Transactional(readOnly = true)
    public List<CancellationDto> getCancellationsByDateRange(LocalDateTime start, LocalDateTime end) {
        log.debug("Fetching cancellations between {} and {}", start, end);
        
        if (start.isAfter(end)) {
            throw new RuntimeException("Start date must be before end date");
        }
        
        List<TicketCancellation> cancellations = cancellationRepository.findByRequestDateBetween(start, end);
        return cancellationMapper.toCancellationDtoList(cancellations);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalRefundAmountByUser(UUID userId) {
        log.debug("Calculating total refund amount for user: {}", userId);
        
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with id: " + userId);
        }
        
        BigDecimal total = cancellationRepository.sumRefundAmountByUserId(userId);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalRefundAmountByMatch(UUID matchId) {
        log.debug("Calculating total refund amount for match: {}", matchId);
        
        BigDecimal total = cancellationRepository.sumRefundAmountByMatchId(matchId);
        return total != null ? total : BigDecimal.ZERO;
    }

    private void validateCancellationRequest(Ticket ticket) {
        if (ticket.getStatus() != TicketStatus.SOLD) {
            throw new RuntimeException("Only sold tickets can be cancelled. Current status: " + ticket.getStatus());
        }

        if (cancellationRepository.existsByTicket_Id(ticket.getId())) {
            throw new RuntimeException("Ticket already has a cancellation request");
        }

        LocalDateTime matchDateTime = ticket.getMatch().getMatchDate();
        if (matchDateTime == null) {
            throw new RuntimeException("Match date is not set for this ticket");
        }

        if (LocalDateTime.now().isAfter(matchDateTime)) {
            throw new RuntimeException("Cannot cancel a ticket for a match that has already passed");
        }
    }

    private BigDecimal calculateRefundAmount(Ticket ticket, int penaltyPercent) {
        BigDecimal finalPrice = ticket.getFinalPrice();
        if (penaltyPercent == 0) {
            return finalPrice;
        }
        
        BigDecimal penaltyAmount = finalPrice.multiply(BigDecimal.valueOf(penaltyPercent))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        
        return finalPrice.subtract(penaltyAmount);
    }

    private BigDecimal calculateCancellationFee(Ticket ticket, int penaltyPercent) {
        if (penaltyPercent == 0) {
            return null;
        }
        
        return ticket.getFinalPrice().multiply(BigDecimal.valueOf(penaltyPercent))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }
}