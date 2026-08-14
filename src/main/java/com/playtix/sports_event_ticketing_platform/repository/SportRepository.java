package com.playtix.sports_event_ticketing_platform.repository;

import com.playtix.sports_event_ticketing_platform.domain.entity.Sport;
import com.playtix.sports_event_ticketing_platform.domain.entity.SportType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class SportRepository {

    private final JdbcTemplate jdbcTemplate;

    public SportRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Sport> sportRowMapper = (rs, rowNum) -> {
        Sport sport = new Sport();
        sport.setId(UUID.fromString(rs.getString("id")));
        
        String nameStr = rs.getString("name");
        if (nameStr != null) {
            sport.setName(SportType.valueOf(nameStr));
        }
        
        sport.setDescription(rs.getString("description"));
        sport.setNumberOfPlayers(rs.getInt("number_of_players"));
        
        return sport;
    };

    // --- Standard CRUD Methods ---

    public List<Sport> findAll() {
        String sql = "SELECT * FROM sports";
        return jdbcTemplate.query(sql, sportRowMapper);
    }

    public Optional<Sport> findById(UUID id) {
        String sql = "SELECT * FROM sports WHERE id = ?";
        List<Sport> results = jdbcTemplate.query(sql, sportRowMapper, id);
        return results.stream().findFirst();
    }

    public Sport save(Sport sport) {
        if (sport.getId() != null && findById(sport.getId()).isPresent()) {
            String sql = "UPDATE sports SET name = ?, description = ?, number_of_players = ? WHERE id = ?";
            jdbcTemplate.update(sql,
                    sport.getName() != null ? sport.getName().name() : null,
                    sport.getDescription(),
                    sport.getNumberOfPlayers(),
                    sport.getId()
            );
        } else {
            String sql = "INSERT INTO sports (id, name, description, number_of_players) VALUES (?, ?, ?, ?)";
            if (sport.getId() == null) {
                sport.setId(UUID.randomUUID());
            }
            jdbcTemplate.update(sql,
                    sport.getId(),
                    sport.getName() != null ? sport.getName().name() : null,
                    sport.getDescription(),
                    sport.getNumberOfPlayers()
            );
        }
        return sport;
    }

    public void deleteById(UUID id) {
        String sql = "DELETE FROM sports WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public void delete(Sport sport) {
        if (sport != null && sport.getId() != null) {
            deleteById(sport.getId());
        }
    }

    public Optional<Sport> findByName(SportType name) {
        String sql = "SELECT * FROM sports WHERE name = ?";
        List<Sport> results = jdbcTemplate.query(sql, sportRowMapper, name.name());
        return results.stream().findFirst();
    }

    public List<Sport> findByDescriptionContainingIgnoreCase(String description) {
        String sql = "SELECT * FROM sports WHERE LOWER(description) LIKE LOWER(?)";
        return jdbcTemplate.query(sql, sportRowMapper, "%" + description + "%");
    }

    public List<Sport> findByNumberOfPlayers(int numberOfPlayers) {
        String sql = "SELECT * FROM sports WHERE number_of_players = ?";
        return jdbcTemplate.query(sql, sportRowMapper, numberOfPlayers);
    }

    public List<Sport> findByNumberOfPlayersBetween(int minPlayers, int maxPlayers) {
        String sql = "SELECT * FROM sports WHERE number_of_players BETWEEN ? AND ?";
        return jdbcTemplate.query(sql, sportRowMapper, minPlayers, maxPlayers);
    }

    public List<Sport> findByNumberOfPlayersGreaterThan(int numberOfPlayers) {
        String sql = "SELECT * FROM sports WHERE number_of_players > ?";
        return jdbcTemplate.query(sql, sportRowMapper, numberOfPlayers);
    }

    public List<Sport> findByNumberOfPlayersLessThan(int numberOfPlayers) {
        String sql = "SELECT * FROM sports WHERE number_of_players < ?";
        return jdbcTemplate.query(sql, sportRowMapper, numberOfPlayers);
    }

    public List<Sport> findAllByOrderByNameAsc() {
        String sql = "SELECT * FROM sports ORDER BY name ASC";
        return jdbcTemplate.query(sql, sportRowMapper);
    }

    public Optional<Sport> findByIdWithLeagues(UUID sportId) {
        String sql = "SELECT * FROM sports WHERE id = ?";
        return jdbcTemplate.query(sql, sportRowMapper, sportId).stream().findFirst();
    }

    public Optional<Sport> findByIdWithTournaments(UUID sportId) {
        String sql = "SELECT * FROM sports WHERE id = ?";
        return jdbcTemplate.query(sql, sportRowMapper, sportId).stream().findFirst();
    }

    public Optional<Sport> findByIdWithAllRelationships(UUID sportId) {
        String sql = "SELECT * FROM sports WHERE id = ?";
        return jdbcTemplate.query(sql, sportRowMapper, sportId).stream().findFirst();
    }

    public List<Sport> findSportsWithLeagues() {
        String sql = "SELECT DISTINCT s.* FROM sports s JOIN leagues l ON s.id = l.sport_id";
        return jdbcTemplate.query(sql, sportRowMapper);
    }

    public List<Sport> findSportsWithTournaments() {
        String sql = "SELECT DISTINCT s.* FROM sports s JOIN tournaments t ON s.id = t.sport_id";
        return jdbcTemplate.query(sql, sportRowMapper);
    }

    public List<Sport> findSportsWithAtLeastOneLeague() {
        String sql = "SELECT DISTINCT s.* FROM sports s JOIN leagues l ON s.id = l.sport_id";
        return jdbcTemplate.query(sql, sportRowMapper);
    }

    public List<Sport> findSportsWithAtLeastOneTournament() {
        String sql = "SELECT DISTINCT s.* FROM sports s JOIN tournaments t ON s.id = t.sport_id";
        return jdbcTemplate.query(sql, sportRowMapper);
    }

    public List<Sport> findSportsWithoutLeagues() {
        String sql = "SELECT * FROM sports WHERE id NOT IN (SELECT DISTINCT sport_id FROM leagues WHERE sport_id IS NOT NULL)";
        return jdbcTemplate.query(sql, sportRowMapper);
    }

    public List<Sport> findSportsWithoutTournaments() {
        String sql = "SELECT * FROM sports WHERE id NOT IN (SELECT DISTINCT sport_id FROM tournaments WHERE sport_id IS NOT NULL)";
        return jdbcTemplate.query(sql, sportRowMapper);
    }

    public List<Sport> findSportsWithoutAnyAssociation() {
        String sql = "SELECT * FROM sports WHERE id NOT IN (SELECT DISTINCT sport_id FROM leagues WHERE sport_id IS NOT NULL) " +
                     "AND id NOT IN (SELECT DISTINCT sport_id FROM tournaments WHERE sport_id IS NOT NULL)";
        return jdbcTemplate.query(sql, sportRowMapper);
    }

    public long countLeaguesBySportId(UUID sportId) {
        String sql = "SELECT COUNT(id) FROM leagues WHERE sport_id = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, sportId);
        return count != null ? count : 0L;
    }

    public long countTournamentsBySportId(UUID sportId) {
        String sql = "SELECT COUNT(id) FROM tournaments WHERE sport_id = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, sportId);
        return count != null ? count : 0L;
    }

    public List<Object[]> countLeaguesBySport() {
        String sql = "SELECT s.id, COUNT(l.id) FROM sports s LEFT JOIN leagues l ON s.id = l.sport_id GROUP BY s.id ORDER BY COUNT(l.id) DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Object[]{rs.getObject(1), rs.getLong(2)});
    }

    public List<Object[]> countTournamentsBySport() {
        String sql = "SELECT s.id, COUNT(t.id) FROM sports s LEFT JOIN tournaments t ON s.id = t.sport_id GROUP BY s.id ORDER BY COUNT(t.id) DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Object[]{rs.getObject(1), rs.getLong(2)});
    }

    public List<Object[]> countTotalAssociationsBySport() {
        String sql = "SELECT s.id, (SELECT COUNT(l.id) FROM leagues l WHERE l.sport_id = s.id) + (SELECT COUNT(t.id) FROM tournaments t WHERE t.sport_id = s.id) as total " +
                     "FROM sports s ORDER BY total DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Object[]{rs.getObject(1), rs.getLong(2)});
    }

    public List<Sport> findWithDescription() {
        String sql = "SELECT * FROM sports WHERE description IS NOT NULL AND description != ''";
        return jdbcTemplate.query(sql, sportRowMapper);
    }

    public List<Sport> findWithoutDescription() {
        String sql = "SELECT * FROM sports WHERE description IS NULL OR description = ''";
        return jdbcTemplate.query(sql, sportRowMapper);
    }

    public List<Sport> findSportsWithMostPlayers() {
        String sql = "SELECT * FROM sports WHERE number_of_players = (SELECT MAX(number_of_players) FROM sports)";
        return jdbcTemplate.query(sql, sportRowMapper);
    }

    public List<Sport> findSportsWithFewestPlayers() {
        String sql = "SELECT * FROM sports WHERE number_of_players = (SELECT MIN(number_of_players) FROM sports)";
        return jdbcTemplate.query(sql, sportRowMapper);
    }

    public Double averageNumberOfPlayers() {
        String sql = "SELECT AVG(number_of_players) FROM sports";
        return jdbcTemplate.queryForObject(sql, Double.class);
    }

    public List<Sport> findSportsAboveAveragePlayers() {
        String sql = "SELECT * FROM sports WHERE number_of_players > (SELECT AVG(number_of_players) FROM sports)";
        return jdbcTemplate.query(sql, sportRowMapper);
    }

    public List<Sport> findSportsBelowAveragePlayers() {
        String sql = "SELECT * FROM sports WHERE number_of_players < (SELECT AVG(number_of_players) FROM sports)";
        return jdbcTemplate.query(sql, sportRowMapper);
    }

    public List<Object[]> countLeaguesBySportName() {
        String sql = "SELECT s.name, COUNT(l.id) FROM sports s LEFT JOIN leagues l ON s.id = l.sport_id GROUP BY s.name";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Object[]{rs.getString(1), rs.getLong(2)});
    }

    public List<Object[]> countTournamentsBySportName() {
        String sql = "SELECT s.name, COUNT(t.id) FROM sports s LEFT JOIN tournaments t ON s.id = t.sport_id GROUP BY s.name";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Object[]{rs.getString(1), rs.getLong(2)});
    }

    public List<Sport> findSportsWithBothLeaguesAndTournaments() {
        String sql = "SELECT DISTINCT s.* FROM sports s JOIN leagues l ON s.id = l.sport_id JOIN tournaments t ON s.id = t.sport_id";
        return jdbcTemplate.query(sql, sportRowMapper);
    }

    public List<Sport> findSportsWithLeaguesOrTournaments() {
        String sql = "SELECT DISTINCT s.* FROM sports s LEFT JOIN leagues l ON s.id = l.sport_id LEFT JOIN tournaments t ON s.id = t.sport_id WHERE l.id IS NOT NULL OR t.id IS NOT NULL";
        return jdbcTemplate.query(sql, sportRowMapper);
    }

    public boolean existsByName(SportType name) {
        String sql = "SELECT COUNT(id) FROM sports WHERE name = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, name.name());
        return count != null && count > 0;
    }

    public boolean existsBySportType(SportType name) {
        String sql = "SELECT COUNT(id) FROM sports WHERE name = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, name.name());
        return count != null && count > 0;
    }

    public List<Sport> findAllOrderByNumberOfPlayersDesc() {
        String sql = "SELECT * FROM sports ORDER BY number_of_players DESC";
        return jdbcTemplate.query(sql, sportRowMapper);
    }

    public List<Sport> searchByDescriptionKeyword(String keyword) {
        String sql = "SELECT * FROM sports WHERE LOWER(description) LIKE LOWER(?)";
        return jdbcTemplate.query(sql, sportRowMapper, "%" + keyword + "%");
    }
}