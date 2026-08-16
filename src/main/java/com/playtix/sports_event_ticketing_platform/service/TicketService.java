package com.playtix.sports_event_ticketing_platform.service;

import com.playtix.sports_event_ticketing_platform.domain.dto.ticket.CreateTicketRequest;
import com.playtix.sports_event_ticketing_platform.domain.dto.ticket.TicketDetailsDto;
import com.playtix.sports_event_ticketing_platform.domain.dto.ticket.TicketSummaryDto;
import com.playtix.sports_event_ticketing_platform.elastic.ElasticTicketSyncService;
import com.playtix.sports_event_ticketing_platform.domain.entity.ticket.Ticket;
import com.playtix.sports_event_ticketing_platform.domain.entity.ticket.TicketCategory;
import com.playtix.sports_event_ticketing_platform.domain.entity.ticket.TicketStatus;
import com.playtix.sports_event_ticketing_platform.mapper.TicketMapper;
import com.playtix.sports_event_ticketing_platform.domain.entity.match.Match;
import com.playtix.sports_event_ticketing_platform.repository.MatchRepository;
import com.playtix.sports_event_ticketing_platform.repository.TicketCategoryRepository;
import com.playtix.sports_event_ticketing_platform.repository.TicketRepository;
import com.playtix.sports_event_ticketing_platform.service.redis.RedisCacheService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TicketCategoryRepository ticketCategoryRepository;
    private final MatchRepository matchRepository;
    private final TicketMapper ticketMapper;
    private final RedisCacheService redisCacheService;
    private final ElasticTicketSyncService elasticTicketSyncService;

    @Transactional
    public TicketSummaryDto createTicket(CreateTicketRequest request) {
        TicketCategory category = ticketCategoryRepository.findById(request.ticketCategoryId())
                .orElseThrow(() -> new RuntimeException("Ticket category not found"));

        Match match = matchRepository.findById(request.matchId())
                .orElseThrow(() -> new RuntimeException("Match not found"));

        Ticket ticket = Ticket.builder()
                .seatNumber(request.seatNumber())
                .rowNumber(request.rowNumber())
                .sectionNumber(request.sectionNumber())
                .price(request.price())
                .finalPrice(request.price())
                .status(TicketStatus.NOT_RESERVED)
                .ticketCategory(category)
                .match(match)
                .build();

        ticket = ticketRepository.save(ticket);
        category.addTicket(ticket);

        // sync SQL ticket into Elasticsearch index
        elasticTicketSyncService.indexTicket(ticket);

        // invalidate broad search cache after ticket creation
        redisCacheService.deleteByPattern("ticket-search:*");

        return ticketMapper.toSummaryDto(ticket);
    }

    @Transactional(readOnly = true)
    public List<TicketSummaryDto> getTicketsSummary() {
        List<Ticket> tickets = ticketRepository.findAll();
        return ticketMapper.toSummaryDtoList(tickets);
    }

    @Transactional(readOnly = true)
    public TicketSummaryDto getTicketSummary(UUID ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        return ticketMapper.toSummaryDto(ticket);
    }

    @Transactional(readOnly = true)
    public TicketDetailsDto getTicketDetails(UUID ticketId) {
        Ticket ticket = ticketRepository.findByIdWithMatchAndCategory(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        return ticketMapper.toDetailsDto(ticket);
    }

    @Transactional(readOnly = true)
    public List<TicketSummaryDto> getTicketsByCategory(UUID categoryId) {
        List<Ticket> tickets = ticketRepository.findByTicketCategory_Id(categoryId);
        return ticketMapper.toSummaryDtoList(tickets);
    }

    @Transactional(readOnly = true)
    public List<TicketSummaryDto> getAvailableTicketsByCategory(UUID categoryId) {
        List<Ticket> tickets = ticketRepository.findByTicketCategory_IdAndStatus(categoryId, TicketStatus.NOT_RESERVED);
        return ticketMapper.toSummaryDtoList(tickets);
    }

    @Transactional(readOnly = true)
    public List<TicketSummaryDto> getTicketsByMatch(UUID matchId) {
        List<Ticket> tickets = ticketRepository.findByMatch_Id(matchId);
        return ticketMapper.toSummaryDtoList(tickets);
    }

    @Transactional(readOnly = true)
    public List<TicketSummaryDto> getAvailableTicketsByMatch(UUID matchId) {
        List<Ticket> tickets = ticketRepository.findByMatch_IdAndStatus(matchId, TicketStatus.NOT_RESERVED);
        return ticketMapper.toSummaryDtoList(tickets);
    }

    @Transactional(readOnly = true)
    public TicketSummaryDto getTicketByBarcode(String barcode) {
        Ticket ticket = ticketRepository.findByBarcode(barcode)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        return ticketMapper.toSummaryDto(ticket);
    }

    @Transactional(readOnly = true)
    public List<TicketSummaryDto> getTicketsByUserId(UUID userId) {
        List<Ticket> tickets = ticketRepository.findTicketsByUserId(userId);
        return ticketMapper.toSummaryDtoList(tickets);
    }

    @Transactional(readOnly = true)
    public List<TicketSummaryDto> getPurchasedTicketsByUserId(UUID userId) {
        List<Ticket> tickets = ticketRepository.findPurchasedTicketsByUserId(userId);
        return ticketMapper.toSummaryDtoList(tickets);
    }

    @Transactional(readOnly = true)
    public List<TicketSummaryDto> getReservedTicketsByUserId(UUID userId) {
        List<Ticket> tickets = ticketRepository.findReservedTicketsByUserId(userId);
        return ticketMapper.toSummaryDtoList(tickets);
    }
}