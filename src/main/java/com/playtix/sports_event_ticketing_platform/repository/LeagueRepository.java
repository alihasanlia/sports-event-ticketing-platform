package com.playtix.sports_event_ticketing_platform.repository;

import com.playtix.sports_event_ticketing_platform.domain.entity.League;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class LeagueRepository {

    private final JdbcTemplate jdbcTemplate;

    public LeagueRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<League> leagueRowMapper = (rs, rowNum) -> {
        League league = new League();
        league.setId((UUID) rs.getObject("id"));
        league.setName(rs.getString("name"));
        league.setCountry(rs.getString("country"));
        league.setSeason(rs.getString("season"));
        league.setNumberOfTeams((Integer) rs.getObject("number_of_teams"));
        league.setDescription(rs.getString("description"));
        return league;
    };

    public Optional<League> findById(UUID id) {
        String sql = "SELECT id, name, country, season, number_of_teams, description FROM leagues WHERE id = ?";
        List<League> results = jdbcTemplate.query(sql, leagueRowMapper, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public void deleteById(UUID id) {
        String sql = "DELETE FROM leagues WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public void delete(League league) {
        if (league != null && league.getId() != null) {
            deleteById(league.getId());
        }
    }

    public League save(League league) {
        if (league.getId() == null) {
            league.setId(UUID.randomUUID());
            String sql = "INSERT INTO leagues (id, name, country, season, number_of_teams, description, sport_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(sql, 
                league.getId(), 
                league.getName(), 
                league.getCountry(), 
                league.getSeason(), 
                league.getNumberOfTeams(), 
                league.getDescription(), 
                league.getSport() != null ? league.getSport().getId() : null
            );
        } else {
            String sql = "UPDATE leagues SET name = ?, country = ?, season = ?, number_of_teams = ?, description = ?, sport_id = ? WHERE id = ?";
            jdbcTemplate.update(sql, 
                league.getName(), 
                league.getCountry(), 
                league.getSeason(), 
                league.getNumberOfTeams(), 
                league.getDescription(), 
                league.getSport() != null ? league.getSport().getId() : null, 
                league.getId()
            );
        }
        return league;
    }

    public List<League> findAll() {
        String sql = "SELECT id, name, country, season, number_of_teams, description FROM leagues";
        return jdbcTemplate.query(sql, leagueRowMapper);
    }

    public Optional<League> findByName(String name) {
        String sql = "SELECT id, name, country, season, number_of_teams, description FROM leagues WHERE name = ?";
        List<League> results = jdbcTemplate.query(sql, leagueRowMapper, name);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public List<League> findByNameContainingIgnoreCase(String name) {
        String sql = "SELECT id, name, country, season, number_of_teams, description FROM leagues WHERE LOWER(name) LIKE LOWER(?)";
        return jdbcTemplate.query(sql, leagueRowMapper, "%" + name + "%");
    }

    public List<League> findByCountry(String country) {
        String sql = "SELECT id, name, country, season, number_of_teams, description FROM leagues WHERE country = ?";
        return jdbcTemplate.query(sql, leagueRowMapper, country);
    }

    public List<League> findByCountryContainingIgnoreCase(String country) {
        String sql = "SELECT id, name, country, season, number_of_teams, description FROM leagues WHERE LOWER(country) LIKE LOWER(?)";
        return jdbcTemplate.query(sql, leagueRowMapper, "%" + country + "%");
    }

    public List<League> findBySeason(String season) {
        String sql = "SELECT id, name, country, season, number_of_teams, description FROM leagues WHERE season = ?";
        return jdbcTemplate.query(sql, leagueRowMapper, season);
    }

    public List<League> findBySeasonContainingIgnoreCase(String season) {
        String sql = "SELECT id, name, country, season, number_of_teams, description FROM leagues WHERE LOWER(season) LIKE LOWER(?)";
        return jdbcTemplate.query(sql, leagueRowMapper, "%" + season + "%");
    }

    public List<League> findBySport_Id(UUID sportId) {
        String sql = "SELECT id, name, country, season, number_of_teams, description FROM leagues WHERE sport_id = ?";
        return jdbcTemplate.query(sql, leagueRowMapper, sportId);
    }

    public List<League> findBySport_IdOrderByNameAsc(UUID sportId) {
        String sql = "SELECT id, name, country, season, number_of_teams, description FROM leagues WHERE sport_id = ? ORDER BY name ASC";
        return jdbcTemplate.query(sql, leagueRowMapper, sportId);
    }

    public List<League> findByNumberOfTeams(Integer numberOfTeams) {
        String sql = "SELECT id, name, country, season, number_of_teams, description FROM leagues WHERE number_of_teams = ?";
        return jdbcTemplate.query(sql, leagueRowMapper, numberOfTeams);
    }

    public List<League> findByNumberOfTeamsBetween(Integer minTeams, Integer maxTeams) {
        String sql = "SELECT id, name, country, season, number_of_teams, description FROM leagues WHERE number_of_teams BETWEEN ? AND ?";
        return jdbcTemplate.query(sql, leagueRowMapper, minTeams, maxTeams);
    }

    public List<League> findByNumberOfTeamsGreaterThan(Integer numberOfTeams) {
        String sql = "SELECT id, name, country, season, number_of_teams, description FROM leagues WHERE number_of_teams > ?";
        return jdbcTemplate.query(sql, leagueRowMapper, numberOfTeams);
    }

    public List<League> findByNumberOfTeamsLessThan(Integer numberOfTeams) {
        String sql = "SELECT id, name, country, season, number_of_teams, description FROM leagues WHERE number_of_teams < ?";
        return jdbcTemplate.query(sql, leagueRowMapper, numberOfTeams);
    }

    public List<League> findAllByOrderByNameAsc() {
        String sql = "SELECT id, name, country, season, number_of_teams, description FROM leagues ORDER BY name ASC";
        return jdbcTemplate.query(sql, leagueRowMapper);
    }

    public List<League> findAllByOrderByCountryAsc() {
        String sql = "SELECT id, name, country, season, number_of_teams, description FROM leagues ORDER BY country ASC";
        return jdbcTemplate.query(sql, leagueRowMapper);
    }

    public Optional<League> findByIdWithMatches(UUID leagueId) {
        String sql = "SELECT id, name, country, season, number_of_teams, description FROM leagues WHERE id = ?";
        List<League> results = jdbcTemplate.query(sql, leagueRowMapper, leagueId);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public Optional<League> findByIdWithSport(UUID leagueId) {
        String sql = "SELECT id, name, country, season, number_of_teams, description FROM leagues WHERE id = ?";
        List<League> results = jdbcTemplate.query(sql, leagueRowMapper, leagueId);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public Optional<League> findByIdWithAllRelationships(UUID leagueId) {
        String sql = "SELECT id, name, country, season, number_of_teams, description FROM leagues WHERE id = ?";
        List<League> results = jdbcTemplate.query(sql, leagueRowMapper, leagueId);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public List<League> findLeaguesWithUpcomingMatches(LocalDateTime now) {
        String sql = "SELECT DISTINCT l.id, l.name, l.country, l.season, l.number_of_teams, l.description FROM leagues l JOIN matches m ON l.id = m.league_id WHERE m.match_date > ?";
        return jdbcTemplate.query(sql, leagueRowMapper, now);
    }

    public List<League> findLeaguesWithFinishedMatches(LocalDateTime now) {
        String sql = "SELECT DISTINCT l.id, l.name, l.country, l.season, l.number_of_teams, l.description FROM leagues l JOIN matches m ON l.id = m.league_id WHERE m.match_date < ?";
        return jdbcTemplate.query(sql, leagueRowMapper, now);
    }

    public List<League> findLeaguesWithFutureMatches(LocalDateTime now) {
        String sql = "SELECT id, name, country, season, number_of_teams, description FROM leagues WHERE id IN (SELECT DISTINCT m.league_id FROM matches m WHERE m.match_date > ?)";
        return jdbcTemplate.query(sql, leagueRowMapper, now);
    }

    public List<League> findLeaguesWithPastMatches(LocalDateTime now) {
        String sql = "SELECT id, name, country, season, number_of_teams, description FROM leagues WHERE id IN (SELECT DISTINCT m.league_id FROM matches m WHERE m.match_date < ?)";
        return jdbcTemplate.query(sql, leagueRowMapper, now);
    }

    public List<League> findLeaguesWithAnyMatch() {
        String sql = "SELECT id, name, country, season, number_of_teams, description FROM leagues WHERE id IN (SELECT DISTINCT m.league_id FROM matches m)";
        return jdbcTemplate.query(sql, leagueRowMapper);
    }

    public List<League> findLeaguesWithoutMatches() {
        String sql = "SELECT id, name, country, season, number_of_teams, description FROM leagues WHERE id NOT IN (SELECT DISTINCT m.league_id FROM matches m)";
        return jdbcTemplate.query(sql, leagueRowMapper);
    }

    public long countUpcomingMatchesByLeagueId(UUID leagueId, LocalDateTime now) {
        String sql = "SELECT COUNT(*) FROM leagues l JOIN matches m ON l.id = m.league_id WHERE l.id = ? AND m.match_date > ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, leagueId, now);
        return count != null ? count : 0L;
    }

    public long countFinishedMatchesByLeagueId(UUID leagueId, LocalDateTime now) {
        String sql = "SELECT COUNT(*) FROM leagues l JOIN matches m ON l.id = m.league_id WHERE l.id = ? AND m.match_date < ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, leagueId, now);
        return count != null ? count : 0L;
    }

    public long countAllMatchesByLeagueId(UUID leagueId) {
        String sql = "SELECT COUNT(*) FROM leagues l JOIN matches m ON l.id = m.league_id WHERE l.id = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, leagueId);
        return count != null ? count : 0L;
    }

    public List<Object[]> countMatchesByLeague() {
        String sql = "SELECT l.id, COUNT(m.id) FROM leagues l LEFT JOIN matches m ON l.id = m.league_id GROUP BY l.id ORDER BY COUNT(m.id) DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Object[] {
            rs.getObject(1),
            rs.getLong(2)
        });
    }

    public List<Object[]> countLeaguesByCountry() {
        String sql = "SELECT country, COUNT(*) FROM leagues GROUP BY country";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Object[] {
            rs.getString(1),
            rs.getLong(2)
        });
    }

    public List<Object[]> countLeaguesBySport() {
        String sql = "SELECT sport_id, COUNT(*) FROM leagues GROUP BY sport_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Object[] {
            rs.getObject(1),
            rs.getLong(2)
        });
    }

    public List<League> findWithDescription() {
        String sql = "SELECT id, name, country, season, number_of_teams, description FROM leagues WHERE description IS NOT NULL AND description != ''";
        return jdbcTemplate.query(sql, leagueRowMapper);
    }

    public List<League> findWithoutDescription() {
        String sql = "SELECT id, name, country, season, number_of_teams, description FROM leagues WHERE description IS NULL OR description = ''";
        return jdbcTemplate.query(sql, leagueRowMapper);
    }

    public List<League> findLeaguesWithMostTeams() {
        String sql = "SELECT id, name, country, season, number_of_teams, description FROM leagues WHERE number_of_teams = (SELECT MAX(l2.number_of_teams) FROM leagues l2)";
        return jdbcTemplate.query(sql, leagueRowMapper);
    }

    public List<League> findLeaguesWithFewestTeams() {
        String sql = "SELECT id, name, country, season, number_of_teams, description FROM leagues WHERE number_of_teams = (SELECT MIN(l2.number_of_teams) FROM leagues l2)";
        return jdbcTemplate.query(sql, leagueRowMapper);
    }

    public Double averageNumberOfTeams() {
        String sql = "SELECT AVG(number_of_teams) FROM leagues WHERE number_of_teams IS NOT NULL";
        return jdbcTemplate.queryForObject(sql, Double.class);
    }

    public List<League> findBySportName(String sportName) {
        String sql = "SELECT l.id, l.name, l.country, l.season, l.number_of_teams, l.description FROM leagues l JOIN sports s ON l.sport_id = s.id WHERE s.name = ?";
        return jdbcTemplate.query(sql, leagueRowMapper, sportName);
    }

    public List<League> findBySportIdAndCountry(UUID sportId, String country) {
        String sql = "SELECT id, name, country, season, number_of_teams, description FROM leagues WHERE sport_id = ? AND country = ?";
        return jdbcTemplate.query(sql, leagueRowMapper, sportId, country);
    }

    public List<League> findBySportIdAndSeason(UUID sportId, String season) {
        String sql = "SELECT id, name, country, season, number_of_teams, description FROM leagues WHERE sport_id = ? AND season = ?";
        return jdbcTemplate.query(sql, leagueRowMapper, sportId, season);
    }

    public List<League> findByCountryAndSeason(String country, String season) {
        String sql = "SELECT id, name, country, season, number_of_teams, description FROM leagues WHERE country = ? AND season = ?";
        return jdbcTemplate.query(sql, leagueRowMapper, country, season);
    }

    public List<League> searchByNameOrCountry(String keyword) {
        String sql = "SELECT id, name, country, season, number_of_teams, description FROM leagues WHERE name LIKE ? OR country LIKE ?";
        String pattern = "%" + keyword + "%";
        return jdbcTemplate.query(sql, leagueRowMapper, pattern, pattern);
    }

    public boolean existsByNameIgnoreCase(String name) {
        String sql = "SELECT COUNT(*) FROM leagues WHERE LOWER(name) = LOWER(?)";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, name);
        return count != null && count > 0;
    }

    public boolean existsByNameAndCountry(String name, String country) {
        String sql = "SELECT COUNT(*) FROM leagues WHERE name = ? AND country = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, name, country);
        return count != null && count > 0;
    }

    public List<League> findBySportIdOrderByNumberOfTeamsDesc(UUID sportId) {
        String sql = "SELECT id, name, country, season, number_of_teams, description FROM leagues WHERE sport_id = ? AND number_of_teams IS NOT NULL ORDER BY number_of_teams DESC";
        return jdbcTemplate.query(sql, leagueRowMapper, sportId);
    }
}