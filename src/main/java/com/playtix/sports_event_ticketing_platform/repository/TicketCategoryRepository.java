package com.playtix.sports_event_ticketing_platform.repository;

import com.playtix.sports_event_ticketing_platform.domain.entity.match.Match;
import com.playtix.sports_event_ticketing_platform.domain.entity.ticket.Category;
import com.playtix.sports_event_ticketing_platform.domain.entity.ticket.TicketCategory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TicketCategoryRepository {

    private final JdbcTemplate jdbcTemplate;

    public TicketCategoryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final String SELECT_COLUMNS = "SELECT id, category, price, total_capacity, remaining_capacity, description, match_id FROM ticket_categories";

    private final RowMapper<TicketCategory> ticketCategoryRowMapper = (rs, rowNum) -> {
        TicketCategory tc = new TicketCategory();
        tc.setId((UUID) rs.getObject("id"));

        String categoryStr = rs.getString("category");
        if (categoryStr != null) {
            tc.setCategory(Category.valueOf(categoryStr));
        }

        tc.setPrice(rs.getBigDecimal("price"));
        tc.setTotalCapacity(rs.getInt("total_capacity"));
        tc.setRemainingCapacity(rs.getInt("remaining_capacity"));
        tc.setDescription(rs.getString("description"));

        UUID matchId = (UUID) rs.getObject("match_id");
        if (matchId != null) {
            Match match = new Match();
            match.setId(matchId);
            tc.setMatch(match);
        }

        return tc;
    };

    public Optional<TicketCategory> findById(UUID id) {
        String sql = SELECT_COLUMNS + " WHERE id = ?";
        List<TicketCategory> results = jdbcTemplate.query(sql, ticketCategoryRowMapper, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public void deleteById(UUID id) {
        String sql = "DELETE FROM ticket_categories WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public void delete(TicketCategory ticketCategory) {
        if (ticketCategory != null && ticketCategory.getId() != null) {
            deleteById(ticketCategory.getId());
        }
    }

    public TicketCategory save(TicketCategory ticketCategory) {
        if (ticketCategory.getId() == null) {
            ticketCategory.setId(UUID.randomUUID());
            String sql = "INSERT INTO ticket_categories (id, category, price, total_capacity, remaining_capacity, description, match_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(sql,
                ticketCategory.getId(),
                ticketCategory.getCategory() != null ? ticketCategory.getCategory().name() : null,
                ticketCategory.getPrice(),
                ticketCategory.getTotalCapacity(),
                ticketCategory.getRemainingCapacity(),
                ticketCategory.getDescription(),
                ticketCategory.getMatch() != null ? ticketCategory.getMatch().getId() : null
            );
        } else {
            String sql = "UPDATE ticket_categories SET category = ?, price = ?, total_capacity = ?, remaining_capacity = ?, description = ?, match_id = ? WHERE id = ?";
            jdbcTemplate.update(sql,
                ticketCategory.getCategory() != null ? ticketCategory.getCategory().name() : null,
                ticketCategory.getPrice(),
                ticketCategory.getTotalCapacity(),
                ticketCategory.getRemainingCapacity(),
                ticketCategory.getDescription(),
                ticketCategory.getMatch() != null ? ticketCategory.getMatch().getId() : null,
                ticketCategory.getId()
            );
        }
        return ticketCategory;
    }

    public List<TicketCategory> findAll() {
        return jdbcTemplate.query(SELECT_COLUMNS, ticketCategoryRowMapper);
    }

    public List<TicketCategory> findByMatch_Id(UUID matchId) {
        String sql = SELECT_COLUMNS + " WHERE match_id = ?";
        return jdbcTemplate.query(sql, ticketCategoryRowMapper, matchId);
    }

    public List<TicketCategory> findByMatch_IdOrderByPriceAsc(UUID matchId) {
        String sql = SELECT_COLUMNS + " WHERE match_id = ? ORDER BY price ASC";
        return jdbcTemplate.query(sql, ticketCategoryRowMapper, matchId);
    }

    public List<TicketCategory> findByMatch_IdOrderByPriceDesc(UUID matchId) {
        String sql = SELECT_COLUMNS + " WHERE match_id = ? ORDER BY price DESC";
        return jdbcTemplate.query(sql, ticketCategoryRowMapper, matchId);
    }

    public List<TicketCategory> findByCategory(Category category) {
        String sql = SELECT_COLUMNS + " WHERE category = ?";
        return jdbcTemplate.query(sql, ticketCategoryRowMapper, category != null ? category.name() : null);
    }

    public List<TicketCategory> findByCategoryAndMatch_Id(Category category, UUID matchId) {
        String sql = SELECT_COLUMNS + " WHERE category = ? AND match_id = ?";
        return jdbcTemplate.query(sql, ticketCategoryRowMapper, category != null ? category.name() : null, matchId);
    }

    public List<TicketCategory> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        String sql = SELECT_COLUMNS + " WHERE price BETWEEN ? AND ?";
        return jdbcTemplate.query(sql, ticketCategoryRowMapper, minPrice, maxPrice);
    }

    public List<TicketCategory> findByPriceLessThan(BigDecimal price) {
        String sql = SELECT_COLUMNS + " WHERE price < ?";
        return jdbcTemplate.query(sql, ticketCategoryRowMapper, price);
    }

    public List<TicketCategory> findByPriceGreaterThan(BigDecimal price) {
        String sql = SELECT_COLUMNS + " WHERE price > ?";
        return jdbcTemplate.query(sql, ticketCategoryRowMapper, price);
    }

    public List<TicketCategory> findByTotalCapacityGreaterThan(int capacity) {
        String sql = SELECT_COLUMNS + " WHERE total_capacity > ?";
        return jdbcTemplate.query(sql, ticketCategoryRowMapper, capacity);
    }

    public List<TicketCategory> findByRemainingCapacityGreaterThan(int capacity) {
        String sql = SELECT_COLUMNS + " WHERE remaining_capacity > ?";
        return jdbcTemplate.query(sql, ticketCategoryRowMapper, capacity);
    }

    public List<TicketCategory> findByRemainingCapacityEquals(int capacity) {
        String sql = SELECT_COLUMNS + " WHERE remaining_capacity = ?";
        return jdbcTemplate.query(sql, ticketCategoryRowMapper, capacity);
    }

    public List<TicketCategory> findByTotalCapacityEquals(int capacity) {
        String sql = SELECT_COLUMNS + " WHERE total_capacity = ?";
        return jdbcTemplate.query(sql, ticketCategoryRowMapper, capacity);
    }

    public List<TicketCategory> findByMatch_IdAndRemainingCapacityGreaterThan(UUID matchId, int capacity) {
        String sql = SELECT_COLUMNS + " WHERE match_id = ? AND remaining_capacity > ?";
        return jdbcTemplate.query(sql, ticketCategoryRowMapper, matchId, capacity);
    }

    public Optional<TicketCategory> findByIdWithMatchAndTickets(UUID categoryId) {
        return findById(categoryId);
    }

    public Optional<TicketCategory> findByIdWithMatch(UUID categoryId) {
        return findById(categoryId);
    }

    public Optional<TicketCategory> findByIdWithTickets(UUID categoryId) {
        return findById(categoryId);
    }

    public List<TicketCategory> findByMatchIdWithTickets(UUID matchId) {
        return findByMatch_Id(matchId);
    }

    public List<TicketCategory> findAvailableCategoriesByMatchId(UUID matchId) {
        String sql = SELECT_COLUMNS + " WHERE match_id = ? AND remaining_capacity > 0 ORDER BY price ASC";
        return jdbcTemplate.query(sql, ticketCategoryRowMapper, matchId);
    }

    public List<TicketCategory> findSoldOutCategoriesByMatchId(UUID matchId) {
        String sql = SELECT_COLUMNS + " WHERE match_id = ? AND remaining_capacity = 0";
        return jdbcTemplate.query(sql, ticketCategoryRowMapper, matchId);
    }

    public List<TicketCategory> findPartiallySoldCategoriesByMatchId(UUID matchId) {
        String sql = SELECT_COLUMNS + " WHERE match_id = ? AND remaining_capacity < total_capacity";
        return jdbcTemplate.query(sql, ticketCategoryRowMapper, matchId);
    }

    public List<TicketCategory> findAvailableCategoriesSortedByCapacity() {
        String sql = SELECT_COLUMNS + " WHERE remaining_capacity > 0 ORDER BY remaining_capacity DESC";
        return jdbcTemplate.query(sql, ticketCategoryRowMapper);
    }

    public List<TicketCategory> findCategoriesSortedBySoldCountDesc() {
        String sql = SELECT_COLUMNS + " WHERE remaining_capacity < total_capacity ORDER BY (total_capacity - remaining_capacity) DESC";
        return jdbcTemplate.query(sql, ticketCategoryRowMapper);
    }

    public Integer sumRemainingCapacityByMatchId(UUID matchId) {
        String sql = "SELECT SUM(remaining_capacity) FROM ticket_categories WHERE match_id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, matchId);
    }

    public Integer sumTotalCapacityByMatchId(UUID matchId) {
        String sql = "SELECT SUM(total_capacity) FROM ticket_categories WHERE match_id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, matchId);
    }

    public Integer sumSoldTicketsByMatchId(UUID matchId) {
        String sql = "SELECT SUM(total_capacity - remaining_capacity) FROM ticket_categories WHERE match_id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, matchId);
    }

    public List<Object[]> sumSoldTicketsByMatch() {
        String sql = "SELECT match_id, SUM(total_capacity - remaining_capacity) FROM ticket_categories GROUP BY match_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Object[] { rs.getObject(1), rs.getLong(2) });
    }

    public List<Object[]> sumRemainingCapacityByMatch() {
        String sql = "SELECT match_id, SUM(remaining_capacity) FROM ticket_categories GROUP BY match_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Object[] { rs.getObject(1), rs.getLong(2) });
    }

    public List<Object[]> sumTotalCapacityByMatch() {
        String sql = "SELECT match_id, SUM(total_capacity) FROM ticket_categories GROUP BY match_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Object[] { rs.getObject(1), rs.getLong(2) });
    }

    public List<TicketCategory> findCheapestCategoriesByMatchId(UUID matchId) {
        String sql = SELECT_COLUMNS + " WHERE match_id = ? AND price = (SELECT MIN(tc2.price) FROM ticket_categories tc2 WHERE tc2.match_id = ?)";
        return jdbcTemplate.query(sql, ticketCategoryRowMapper, matchId, matchId);
    }

    public List<TicketCategory> findMostExpensiveCategoriesByMatchId(UUID matchId) {
        String sql = SELECT_COLUMNS + " WHERE match_id = ? AND price = (SELECT MAX(tc2.price) FROM ticket_categories tc2 WHERE tc2.match_id = ?)";
        return jdbcTemplate.query(sql, ticketCategoryRowMapper, matchId, matchId);
    }

    public Optional<TicketCategory> findByMatchIdAndCategory(UUID matchId, Category category) {
        String sql = SELECT_COLUMNS + " WHERE match_id = ? AND category = ?";
        List<TicketCategory> results = jdbcTemplate.query(sql, ticketCategoryRowMapper, matchId, category != null ? category.name() : null);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public long countByMatchId(UUID matchId) {
        String sql = "SELECT COUNT(*) FROM ticket_categories WHERE match_id = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, matchId);
        return count != null ? count : 0L;
    }

    public List<Object[]> countCategoriesByType() {
        String sql = "SELECT category, COUNT(*) FROM ticket_categories GROUP BY category";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Object[] { rs.getString(1), rs.getLong(2) });
    }

    public List<TicketCategory> findWithDescription() {
        String sql = SELECT_COLUMNS + " WHERE description IS NOT NULL";
        return jdbcTemplate.query(sql, ticketCategoryRowMapper);
    }

    public List<TicketCategory> findWithoutDescription() {
        String sql = SELECT_COLUMNS + " WHERE description IS NULL";
        return jdbcTemplate.query(sql, ticketCategoryRowMapper);
    }

    public List<TicketCategory> findUnsoldCategories() {
        String sql = SELECT_COLUMNS + " WHERE total_capacity = remaining_capacity";
        return jdbcTemplate.query(sql, ticketCategoryRowMapper);
    }

    public List<TicketCategory> findFullySoldCategories() {
        String sql = SELECT_COLUMNS + " WHERE remaining_capacity = 0";
        return jdbcTemplate.query(sql, ticketCategoryRowMapper);
    }

    public List<TicketCategory> findPartiallyAvailableCategories() {
        String sql = SELECT_COLUMNS + " WHERE remaining_capacity > 0 AND remaining_capacity < total_capacity";
        return jdbcTemplate.query(sql, ticketCategoryRowMapper);
    }

    public List<TicketCategory> findByMatchIdAndPrice(UUID matchId, BigDecimal price) {
        String sql = SELECT_COLUMNS + " WHERE match_id = ? AND price = ?";
        return jdbcTemplate.query(sql, ticketCategoryRowMapper, matchId, price);
    }

    public boolean existsByMatch_IdAndCategory(UUID matchId, Category category) {
        String sql = "SELECT COUNT(*) FROM ticket_categories WHERE match_id = ? AND category = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, matchId, category != null ? category.name() : null);
        return count != null && count > 0;
    }

    public Optional<Integer> findRemainingCapacityById(UUID categoryId) {
        String sql = "SELECT remaining_capacity FROM ticket_categories WHERE id = ?";
        List<Integer> results = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt(1), categoryId);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
}