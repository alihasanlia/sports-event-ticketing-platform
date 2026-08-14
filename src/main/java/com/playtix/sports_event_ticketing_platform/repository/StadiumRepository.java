package com.playtix.sports_event_ticketing_platform.repository;

import com.playtix.sports_event_ticketing_platform.domain.entity.Stadium;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class StadiumRepository {

    private final JdbcTemplate jdbcTemplate;

    public StadiumRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Stadium> stadiumRowMapper = (rs, rowNum) -> {
        Stadium stadium = new Stadium();
        stadium.setId(UUID.fromString(rs.getString("id")));
        stadium.setName(rs.getString("name"));
        stadium.setCity(rs.getString("city"));
        stadium.setCapacity(rs.getInt("capacity"));
        stadium.setAddress(rs.getString("address"));
        return stadium;
    };

    public List<Stadium> findAll() {
        String sql = "SELECT * FROM stadiums";
        return jdbcTemplate.query(sql, stadiumRowMapper);
    }

    public Optional<Stadium> findById(UUID id) {
        String sql = "SELECT * FROM stadiums WHERE id = ?";
        List<Stadium> results = jdbcTemplate.query(sql, stadiumRowMapper, id);
        return results.stream().findFirst();
    }

    public Stadium save(Stadium stadium) {
        if (stadium.getId() != null && findById(stadium.getId()).isPresent()) {
            String sql = "UPDATE stadiums SET name = ?, city = ?, capacity = ?, address = ? WHERE id = ?";
            jdbcTemplate.update(sql,
                    stadium.getName(),
                    stadium.getCity(),
                    stadium.getCapacity(),
                    stadium.getAddress(),
                    stadium.getId()
            );
        } else {
            String sql = "INSERT INTO stadiums (id, name, city, capacity, address) VALUES (?, ?, ?, ?, ?)";
            if (stadium.getId() == null) {
                stadium.setId(UUID.randomUUID());
            }
            jdbcTemplate.update(sql,
                    stadium.getId(),
                    stadium.getName(),
                    stadium.getCity(),
                    stadium.getCapacity(),
                    stadium.getAddress()
            );
        }
        return stadium;
    }

    public void deleteById(UUID id) {
        String sql = "DELETE FROM stadiums WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public void delete(Stadium stadium) {
        if (stadium != null && stadium.getId() != null) {
            deleteById(stadium.getId());
        }
    }

    // --- Interface Specific Query Methods ---

    public List<Stadium> findByNameContainingIgnoreCase(String name) {
        String sql = "SELECT * FROM stadiums WHERE LOWER(name) LIKE LOWER(?)";
        return jdbcTemplate.query(sql, stadiumRowMapper, "%" + name + "%");
    }

    public List<Stadium> findByCity(String city) {
        String sql = "SELECT * FROM stadiums WHERE city = ?";
        return jdbcTemplate.query(sql, stadiumRowMapper, city);
    }

    public List<Stadium> findByCityContainingIgnoreCase(String city) {
        String sql = "SELECT * FROM stadiums WHERE LOWER(city) LIKE LOWER(?)";
        return jdbcTemplate.query(sql, stadiumRowMapper, "%" + city + "%");
    }

    public List<Stadium> findByCapacityGreaterThanEqual(int capacity) {
        String sql = "SELECT * FROM stadiums WHERE capacity >= ?";
        return jdbcTemplate.query(sql, stadiumRowMapper, capacity);
    }

    public List<Stadium> findByCapacityLessThanEqual(int capacity) {
        String sql = "SELECT * FROM stadiums WHERE capacity <= ?";
        return jdbcTemplate.query(sql, stadiumRowMapper, capacity);
    }

    public List<Stadium> findByCapacityBetween(int minCapacity, int maxCapacity) {
        String sql = "SELECT * FROM stadiums WHERE capacity BETWEEN ? AND ?";
        return jdbcTemplate.query(sql, stadiumRowMapper, minCapacity, maxCapacity);
    }

    public List<Stadium> findByAddressContainingIgnoreCase(String address) {
        String sql = "SELECT * FROM stadiums WHERE LOWER(address) LIKE LOWER(?)";
        return jdbcTemplate.query(sql, stadiumRowMapper, "%" + address + "%");
    }

    public Optional<Stadium> findByName(String name) {
        String sql = "SELECT * FROM stadiums WHERE name = ?";
        List<Stadium> results = jdbcTemplate.query(sql, stadiumRowMapper, name);
        return results.stream().findFirst();
    }

    public List<Stadium> findByCityOrderByNameAsc(String city) {
        String sql = "SELECT * FROM stadiums WHERE city = ? ORDER BY name ASC";
        return jdbcTemplate.query(sql, stadiumRowMapper, city);
    }

    public List<Stadium> findAllByOrderByNameAsc() {
        String sql = "SELECT * FROM stadiums ORDER BY name ASC";
        return jdbcTemplate.query(sql, stadiumRowMapper);
    }

    public List<Stadium> findAllByOrderByCapacityDesc() {
        String sql = "SELECT * FROM stadiums ORDER BY capacity DESC";
        return jdbcTemplate.query(sql, stadiumRowMapper);
    }

    public Optional<Stadium> findByIdWithMatches(UUID stadiumId) {
        return findById(stadiumId);
    }

    public List<Stadium> findStadiumsWithUpcomingMatches(LocalDateTime now) {
        String sql = "SELECT DISTINCT s.* FROM stadiums s JOIN matches m ON s.id = m.stadium_id WHERE m.match_date > ?";
        return jdbcTemplate.query(sql, stadiumRowMapper, Timestamp.valueOf(now));
    }

    public List<Stadium> findStadiumsWithFinishedMatches(LocalDateTime now) {
        String sql = "SELECT DISTINCT s.* FROM stadiums s JOIN matches m ON s.id = m.stadium_id WHERE m.match_date < ?";
        return jdbcTemplate.query(sql, stadiumRowMapper, Timestamp.valueOf(now));
    }

    public List<Stadium> findStadiumsWithFutureMatches(LocalDateTime now) {
        return findStadiumsWithUpcomingMatches(now);
    }

    public List<Stadium> findStadiumsWithPastMatches(LocalDateTime now) {
        return findStadiumsWithFinishedMatches(now);
    }

    public List<Stadium> findStadiumsWithMatchesInDateRange(LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT DISTINCT s.* FROM stadiums s JOIN matches m ON s.id = m.stadium_id WHERE m.match_date BETWEEN ? AND ?";
        return jdbcTemplate.query(sql, stadiumRowMapper, Timestamp.valueOf(start), Timestamp.valueOf(end));
    }

    public List<Stadium> findStadiumsAboveAverageCapacity() {
        String sql = "SELECT * FROM stadiums WHERE capacity > (SELECT AVG(capacity) FROM stadiums)";
        return jdbcTemplate.query(sql, stadiumRowMapper);
    }

    public List<Stadium> findStadiumsBelowAverageCapacity() {
        String sql = "SELECT * FROM stadiums WHERE capacity < (SELECT AVG(capacity) FROM stadiums)";
        return jdbcTemplate.query(sql, stadiumRowMapper);
    }

    public long countUpcomingMatchesByStadiumId(UUID stadiumId, LocalDateTime now) {
        String sql = "SELECT COUNT(id) FROM matches WHERE stadium_id = ? AND match_date > ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, stadiumId, Timestamp.valueOf(now));
        return count != null ? count : 0L;
    }

    public long countFinishedMatchesByStadiumId(UUID stadiumId, LocalDateTime now) {
        String sql = "SELECT COUNT(id) FROM matches WHERE stadium_id = ? AND match_date < ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, stadiumId, Timestamp.valueOf(now));
        return count != null ? count : 0L;
    }

    public List<Object[]> countMatchesByStadium() {
        String sql = "SELECT s.id, COUNT(m.id) FROM stadiums s LEFT JOIN matches m ON s.id = m.stadium_id GROUP BY s.id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Object[]{rs.getObject(1), rs.getLong(2)});
    }

    public List<Object[]> countTicketCategoriesByStadium() {
        String sql = "SELECT s.id, COUNT(tc.id) FROM stadiums s LEFT JOIN matches m ON s.id = m.stadium_id LEFT JOIN ticket_categories tc ON m.id = tc.match_id GROUP BY s.id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Object[]{rs.getObject(1), rs.getLong(2)});
    }

    public List<Object[]> countStadiumsByCity() {
        String sql = "SELECT city, COUNT(id) FROM stadiums GROUP BY city";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Object[]{rs.getString(1), rs.getLong(2)});
    }

    public List<Object[]> averageCapacityByCity() {
        String sql = "SELECT city, AVG(capacity) FROM stadiums GROUP BY city";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Object[]{rs.getString(1), rs.getDouble(2)});
    }

    public List<Stadium> findLargestStadiums() {
        String sql = "SELECT * FROM stadiums WHERE capacity = (SELECT MAX(capacity) FROM stadiums)";
        return jdbcTemplate.query(sql, stadiumRowMapper);
    }

    public List<Stadium> findSmallestStadiums() {
        String sql = "SELECT * FROM stadiums WHERE capacity = (SELECT MIN(capacity) FROM stadiums)";
        return jdbcTemplate.query(sql, stadiumRowMapper);
    }

    public List<Stadium> findByCityAndCapacityGreaterThanEqual(String city, int minCapacity) {
        String sql = "SELECT * FROM stadiums WHERE city = ? AND capacity >= ?";
        return jdbcTemplate.query(sql, stadiumRowMapper, city, minCapacity);
    }

    public List<Stadium> findByCityAndCapacityLessThanEqual(String city, int maxCapacity) {
        String sql = "SELECT * FROM stadiums WHERE city = ? AND capacity <= ?";
        return jdbcTemplate.query(sql, stadiumRowMapper, city, maxCapacity);
    }

    public List<Stadium> findStadiumsWithAddress() {
        String sql = "SELECT * FROM stadiums WHERE address IS NOT NULL AND address != ''";
        return jdbcTemplate.query(sql, stadiumRowMapper);
    }

    public List<Stadium> findStadiumsWithoutAddress() {
        String sql = "SELECT * FROM stadiums WHERE address IS NULL OR address = ''";
        return jdbcTemplate.query(sql, stadiumRowMapper);
    }

    public long countByCity(String city) {
        String sql = "SELECT COUNT(id) FROM stadiums WHERE city = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, city);
        return count != null ? count : 0L;
    }

    public Integer sumTotalCapacity() {
        String sql = "SELECT SUM(capacity) FROM stadiums";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public Integer sumCapacityByCity(String city) {
        String sql = "SELECT SUM(capacity) FROM stadiums WHERE city = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, city);
    }

    public boolean existsByNameIgnoreCase(String name) {
        String sql = "SELECT COUNT(id) FROM stadiums WHERE LOWER(name) = LOWER(?)";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, name);
        return count != null && count > 0;
    }

    public boolean existsByNameAndCity(String name, String city) {
        String sql = "SELECT COUNT(id) FROM stadiums WHERE name = ? AND city = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, name, city);
        return count != null && count > 0;
    }
}