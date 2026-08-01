package com.playtix.sports_event_ticketing_platform.repositories;

import com.playtix.sports_event_ticketing_platform.domain.entity.ticket.Category;
import com.playtix.sports_event_ticketing_platform.domain.entity.ticket.TicketCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TicketCategoryRepository extends JpaRepository<TicketCategory, UUID> {

    List<TicketCategory> findByMatch_Id(UUID matchId);

    List<TicketCategory> findByMatch_IdOrderByPriceAsc(UUID matchId);

    List<TicketCategory> findByMatch_IdOrderByPriceDesc(UUID matchId);

    List<TicketCategory> findByCategory(Category category);

    List<TicketCategory> findByCategoryAndMatch_Id(Category category, UUID matchId);

    List<TicketCategory> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    List<TicketCategory> findByPriceLessThan(BigDecimal price);

    List<TicketCategory> findByPriceGreaterThan(BigDecimal price);

    List<TicketCategory> findByTotalCapacityGreaterThan(int capacity);

    List<TicketCategory> findByRemainingCapacityGreaterThan(int capacity);

    List<TicketCategory> findByRemainingCapacityEquals(int capacity);

    List<TicketCategory> findByTotalCapacityEquals(int capacity);

    List<TicketCategory> findByMatch_IdAndRemainingCapacityGreaterThan(UUID matchId, int capacity);

    @Query("SELECT tc FROM TicketCategory tc LEFT JOIN FETCH tc.match LEFT JOIN FETCH tc.tickets WHERE tc.id = :categoryId")
    Optional<TicketCategory> findByIdWithMatchAndTickets(@Param("categoryId") UUID categoryId);

    @Query("SELECT tc FROM TicketCategory tc LEFT JOIN FETCH tc.match WHERE tc.id = :categoryId")
    Optional<TicketCategory> findByIdWithMatch(@Param("categoryId") UUID categoryId);

    @Query("SELECT tc FROM TicketCategory tc LEFT JOIN FETCH tc.tickets WHERE tc.id = :categoryId")
    Optional<TicketCategory> findByIdWithTickets(@Param("categoryId") UUID categoryId);

    @Query("SELECT tc FROM TicketCategory tc LEFT JOIN FETCH tc.match LEFT JOIN FETCH tc.tickets WHERE tc.match.id = :matchId")
    List<TicketCategory> findByMatchIdWithTickets(@Param("matchId") UUID matchId);

    @Query("SELECT tc FROM TicketCategory tc WHERE tc.match.id = :matchId AND tc.remainingCapacity > 0 ORDER BY tc.price ASC")
    List<TicketCategory> findAvailableCategoriesByMatchId(@Param("matchId") UUID matchId);

    @Query("SELECT tc FROM TicketCategory tc WHERE tc.match.id = :matchId AND tc.remainingCapacity = 0")
    List<TicketCategory> findSoldOutCategoriesByMatchId(@Param("matchId") UUID matchId);

    @Query("SELECT tc FROM TicketCategory tc WHERE tc.match.id = :matchId AND tc.remainingCapacity < tc.totalCapacity")
    List<TicketCategory> findPartiallySoldCategoriesByMatchId(@Param("matchId") UUID matchId);

    @Query("SELECT tc FROM TicketCategory tc WHERE tc.remainingCapacity > 0 ORDER BY tc.remainingCapacity DESC")
    List<TicketCategory> findAvailableCategoriesSortedByCapacity();

    @Query("SELECT tc FROM TicketCategory tc WHERE tc.remainingCapacity < tc.totalCapacity ORDER BY (tc.totalCapacity - tc.remainingCapacity) DESC")
    List<TicketCategory> findCategoriesSortedBySoldCountDesc();

    @Query("SELECT SUM(tc.remainingCapacity) FROM TicketCategory tc WHERE tc.match.id = :matchId")
    Integer sumRemainingCapacityByMatchId(@Param("matchId") UUID matchId);

    @Query("SELECT SUM(tc.totalCapacity) FROM TicketCategory tc WHERE tc.match.id = :matchId")
    Integer sumTotalCapacityByMatchId(@Param("matchId") UUID matchId);

    @Query("SELECT SUM(tc.totalCapacity - tc.remainingCapacity) FROM TicketCategory tc WHERE tc.match.id = :matchId")
    Integer sumSoldTicketsByMatchId(@Param("matchId") UUID matchId);

    @Query("SELECT tc.match.id, SUM(tc.totalCapacity - tc.remainingCapacity) FROM TicketCategory tc GROUP BY tc.match.id")
    List<Object[]> sumSoldTicketsByMatch();

    @Query("SELECT tc.match.id, SUM(tc.remainingCapacity) FROM TicketCategory tc GROUP BY tc.match.id")
    List<Object[]> sumRemainingCapacityByMatch();

    @Query("SELECT tc.match.id, SUM(tc.totalCapacity) FROM TicketCategory tc GROUP BY tc.match.id")
    List<Object[]> sumTotalCapacityByMatch();

    @Query("SELECT tc FROM TicketCategory tc WHERE tc.match.id = :matchId AND tc.price = (SELECT MIN(tc2.price) FROM TicketCategory tc2 WHERE tc2.match.id = :matchId)")
    List<TicketCategory> findCheapestCategoriesByMatchId(@Param("matchId") UUID matchId);

    @Query("SELECT tc FROM TicketCategory tc WHERE tc.match.id = :matchId AND tc.price = (SELECT MAX(tc2.price) FROM TicketCategory tc2 WHERE tc2.match.id = :matchId)")
    List<TicketCategory> findMostExpensiveCategoriesByMatchId(@Param("matchId") UUID matchId);

    @Query("SELECT tc FROM TicketCategory tc WHERE tc.match.id = :matchId AND tc.category = :category")
    Optional<TicketCategory> findByMatchIdAndCategory(@Param("matchId") UUID matchId, @Param("category") Category category);

    @Query("SELECT COUNT(tc) FROM TicketCategory tc WHERE tc.match.id = :matchId")
    long countByMatchId(@Param("matchId") UUID matchId);

    @Query("SELECT tc.category, COUNT(tc) FROM TicketCategory tc GROUP BY tc.category")
    List<Object[]> countCategoriesByType();

    @Query("SELECT tc FROM TicketCategory tc WHERE tc.description IS NOT NULL")
    List<TicketCategory> findWithDescription();

    @Query("SELECT tc FROM TicketCategory tc WHERE tc.description IS NULL")
    List<TicketCategory> findWithoutDescription();

    @Query("SELECT tc FROM TicketCategory tc WHERE tc.totalCapacity = tc.remainingCapacity")
    List<TicketCategory> findUnsoldCategories();

    @Query("SELECT tc FROM TicketCategory tc WHERE tc.remainingCapacity = 0")
    List<TicketCategory> findFullySoldCategories();

    @Query("SELECT tc FROM TicketCategory tc WHERE tc.remainingCapacity > 0 AND tc.remainingCapacity < tc.totalCapacity")
    List<TicketCategory> findPartiallyAvailableCategories();

    @Query("SELECT tc FROM TicketCategory tc WHERE tc.price = :price AND tc.match.id = :matchId")
    List<TicketCategory> findByMatchIdAndPrice(@Param("matchId") UUID matchId, @Param("price") BigDecimal price);

    boolean existsByMatch_IdAndCategory(UUID matchId, Category category);

    @Query("SELECT tc.remainingCapacity FROM TicketCategory tc WHERE tc.id = :categoryId")
    Optional<Integer> findRemainingCapacityById(@Param("categoryId") UUID categoryId);
}
