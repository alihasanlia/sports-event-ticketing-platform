package com.playtix.sports_event_ticketing_platform.service;

import com.playtix.sports_event_ticketing_platform.domain.dto.ticket.CreateTicketCategoryRequest;
import com.playtix.sports_event_ticketing_platform.domain.dto.ticket.TicketCategoryDto;
import com.playtix.sports_event_ticketing_platform.domain.dto.ticket.UpdateTicketCategoryRequest;
import com.playtix.sports_event_ticketing_platform.domain.entity.match.Match;
import com.playtix.sports_event_ticketing_platform.domain.entity.ticket.Category;
import com.playtix.sports_event_ticketing_platform.domain.entity.ticket.TicketCategory;
import com.playtix.sports_event_ticketing_platform.repository.MatchRepository;
import com.playtix.sports_event_ticketing_platform.repository.TicketCategoryRepository;
import com.playtix.sports_event_ticketing_platform.mapper.TicketCategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketCategoryService {

    private final TicketCategoryRepository ticketCategoryRepository;
    private final MatchRepository matchRepository;
    private final TicketCategoryMapper ticketCategoryMapper;

    @Transactional
    public TicketCategoryDto createTicketCategory(CreateTicketCategoryRequest request) {
        Match match = matchRepository.findById(request.matchId())
                .orElseThrow(() -> new RuntimeException("Match not found"));

        if (ticketCategoryRepository.existsByMatch_IdAndCategory(match.getId(), request.category())) {
            throw new RuntimeException("Category already exists for this match");
        }

        TicketCategory category = TicketCategory.builder()
                .category(request.category())
                .price(request.price())
                .totalCapacity(request.totalCapacity())
                .remainingCapacity(request.totalCapacity())
                .description(request.description())
                .match(match)
                .build();

        category = ticketCategoryRepository.save(category);
        return ticketCategoryMapper.toTicketCategoryDto(category);
    }

    @Transactional
    public TicketCategoryDto updateTicketCategory(UpdateTicketCategoryRequest request) {
        TicketCategory category = ticketCategoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new RuntimeException("Ticket category not found"));

        if (request.remainingCapacity() < 0) {
            throw new RuntimeException("Remaining capacity cannot be negative");
        }

        if (request.remainingCapacity() > category.getTotalCapacity()) {
            throw new RuntimeException("Remaining capacity cannot exceed total capacity");
        }

        category.setRemainingCapacity(request.remainingCapacity());
        if (request.description() != null) {
            category.setDescription(request.description());
        }

        category = ticketCategoryRepository.save(category);
        return ticketCategoryMapper.toTicketCategoryDto(category);
    }

    @Transactional(readOnly = true)
    public TicketCategoryDto getTicketCategory(UUID categoryId) {
        TicketCategory category = ticketCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Ticket category not found"));
        return ticketCategoryMapper.toTicketCategoryDto(category);
    }

    @Transactional(readOnly = true)
    public List<TicketCategoryDto> getCategoriesByMatch(UUID matchId) {
        List<TicketCategory> categories = ticketCategoryRepository.findByMatch_Id(matchId);
        return ticketCategoryMapper.toTicketCategoryDtoList(categories);
    }

    @Transactional(readOnly = true)
    public List<TicketCategoryDto> getAvailableCategoriesByMatch(UUID matchId) {
        List<TicketCategory> categories = ticketCategoryRepository.findAvailableCategoriesByMatchId(matchId);
        return ticketCategoryMapper.toTicketCategoryDtoList(categories);
    }

    @Transactional(readOnly = true)
    public List<TicketCategoryDto> getCategoriesByCategory(Category category) {
        List<TicketCategory> categories = ticketCategoryRepository.findByCategory(category);
        return ticketCategoryMapper.toTicketCategoryDtoList(categories);
    }

    @Transactional(readOnly = true)
    public List<TicketCategoryDto> getCategoriesSortedByPriceAsc(UUID matchId) {
        List<TicketCategory> categories = ticketCategoryRepository.findByMatch_IdOrderByPriceAsc(matchId);
        return ticketCategoryMapper.toTicketCategoryDtoList(categories);
    }

    @Transactional(readOnly = true)
    public List<TicketCategoryDto> getCategoriesSortedByPriceDesc(UUID matchId) {
        List<TicketCategory> categories = ticketCategoryRepository.findByMatch_IdOrderByPriceDesc(matchId);
        return ticketCategoryMapper.toTicketCategoryDtoList(categories);
    }

    @Transactional(readOnly = true)
    public List<TicketCategoryDto> getCategoriesByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        List<TicketCategory> categories = ticketCategoryRepository.findByPriceBetween(minPrice, maxPrice);
        return ticketCategoryMapper.toTicketCategoryDtoList(categories);
    }

    @Transactional(readOnly = true)
    public List<TicketCategoryDto> getSoldOutCategories(UUID matchId) {
        List<TicketCategory> categories = ticketCategoryRepository.findSoldOutCategoriesByMatchId(matchId);
        return ticketCategoryMapper.toTicketCategoryDtoList(categories);
    }

    @Transactional(readOnly = true)
    public List<TicketCategoryDto> getPartiallySoldCategories(UUID matchId) {
        List<TicketCategory> categories = ticketCategoryRepository.findPartiallySoldCategoriesByMatchId(matchId);
        return ticketCategoryMapper.toTicketCategoryDtoList(categories);
    }

    @Transactional(readOnly = true)
    public int getRemainingCapacity(UUID categoryId) {
        return ticketCategoryRepository.findRemainingCapacityById(categoryId)
                .orElseThrow(() -> new RuntimeException("Ticket category not found"));
    }

    @Transactional(readOnly = true)
    public int getSoldCount(UUID categoryId) {
        TicketCategory category = ticketCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Ticket category not found"));
        return category.getSoldCount();
    }

    @Transactional(readOnly = true)
    public double getSoldPercentage(UUID categoryId) {
        TicketCategory category = ticketCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Ticket category not found"));
        return category.getSoldPercentage();
    }

    @Transactional
    public void deleteTicketCategory(UUID categoryId) {
        TicketCategory category = ticketCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Ticket category not found"));
        
        if (!category.getTickets().isEmpty()) {
            throw new RuntimeException("Cannot delete category with existing tickets");
        }
        
        ticketCategoryRepository.delete(category);
    }
}