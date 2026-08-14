package com.playtix.sports_event_ticketing_platform.repository;

import com.playtix.sports_event_ticketing_platform.domain.entity.Team;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TeamRepository {

    private final JdbcTemplate jdbcTemplate;

    public TeamRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Team> teamRowMapper = (rs, rowNum) -> {
        Team team = new Team();
        team.setId(UUID.fromString(rs.getString("id")));
        team.setName(rs.getString("name"));
        team.setCity(rs.getString("city"));
        team.setHomeStadium(rs.getString("home_stadium"));
        
        Integer foundedYear = rs.getObject("founded_year", Integer.class);
        team.setFoundedYear(foundedYear);
        
        team.setCoach(rs.getString("coach"));
        team.setLogo(rs.getString("logo"));
        team.setDescription(rs.getString("description"));
        
        return team;
    };

    public List<Team> findAll() {
        String sql = "SELECT * FROM teams";
        return jdbcTemplate.query(sql, teamRowMapper);
    }

    public Optional<Team> findById(UUID id) {
        String sql = "SELECT * FROM teams WHERE id = ?";
        List<Team> results = jdbcTemplate.query(sql, teamRowMapper, id);
        return results.stream().findFirst();
    }

    public Team save(Team team) {
        if (team.getId() != null && findById(team.getId()).isPresent()) {
            String sql = "UPDATE teams SET name = ?, city = ?, home_stadium = ?, founded_year = ?, coach = ?, logo = ?, description = ? WHERE id = ?";
            jdbcTemplate.update(sql,
                    team.getName(),
                    team.getCity(),
                    team.getHomeStadium(),
                    team.getFoundedYear(),
                    team.getCoach(),
                    team.getLogo(),
                    team.getDescription(),
                    team.getId()
            );
        } else {
            String sql = "INSERT INTO teams (id, name, city, home_stadium, founded_year, coach, logo, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            if (team.getId() == null) {
                team.setId(UUID.randomUUID());
            }
            jdbcTemplate.update(sql,
                    team.getId(),
                    team.getName(),
                    team.getCity(),
                    team.getHomeStadium(),
                    team.getFoundedYear(),
                    team.getCoach(),
                    team.getLogo(),
                    team.getDescription()
            );
        }
        return team;
    }

    public void deleteById(UUID id) {
        String sql = "DELETE FROM teams WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public void delete(Team team) {
        if (team != null && team.getId() != null) {
            deleteById(team.getId());
        }
    }

    // --- Interface Specific Query Methods ---

    public Optional<Team> findByName(String name) {
        String sql = "SELECT * FROM teams WHERE name = ?";
        List<Team> results = jdbcTemplate.query(sql, teamRowMapper, name);
        return results.stream().findFirst();
    }

    public List<Team> findByNameContainingIgnoreCase(String name) {
        String sql = "SELECT * FROM teams WHERE LOWER(name) LIKE LOWER(?)";
        return jdbcTemplate.query(sql, teamRowMapper, "%" + name + "%");
    }

    public List<Team> findByCity(String city) {
        String sql = "SELECT * FROM teams WHERE city = ?";
        return jdbcTemplate.query(sql, teamRowMapper, city);
    }

    public List<Team> findByCityContainingIgnoreCase(String city) {
        String sql = "SELECT * FROM teams WHERE LOWER(city) LIKE LOWER(?)";
        return jdbcTemplate.query(sql, teamRowMapper, "%" + city + "%");
    }

    public List<Team> findByHomeStadium(String homeStadium) {
        String sql = "SELECT * FROM teams WHERE home_stadium = ?";
        return jdbcTemplate.query(sql, teamRowMapper, homeStadium);
    }

    public List<Team> findByHomeStadiumContainingIgnoreCase(String homeStadium) {
        String sql = "SELECT * FROM teams WHERE LOWER(home_stadium) LIKE LOWER(?)";
        return jdbcTemplate.query(sql, teamRowMapper, "%" + homeStadium + "%");
    }

    public List<Team> findByFoundedYear(Integer year) {
        String sql = "SELECT * FROM teams WHERE founded_year = ?";
        return jdbcTemplate.query(sql, teamRowMapper, year);
    }

    public List<Team> findByFoundedYearBetween(Integer startYear, Integer endYear) {
        String sql = "SELECT * FROM teams WHERE founded_year BETWEEN ? AND ?";
        return jdbcTemplate.query(sql, teamRowMapper, startYear, endYear);
    }

    public List<Team> findByFoundedYearLessThan(Integer year) {
        String sql = "SELECT * FROM teams WHERE founded_year < ?";
        return jdbcTemplate.query(sql, teamRowMapper, year);
    }

    public List<Team> findByFoundedYearGreaterThan(Integer year) {
        String sql = "SELECT * FROM teams WHERE founded_year > ?";
        return jdbcTemplate.query(sql, teamRowMapper, year);
    }

    public List<Team> findByCoach(String coach) {
        String sql = "SELECT * FROM teams WHERE coach = ?";
        return jdbcTemplate.query(sql, teamRowMapper, coach);
    }

    public List<Team> findByCoachContainingIgnoreCase(String coach) {
        String sql = "SELECT * FROM teams WHERE LOWER(coach) LIKE LOWER(?)";
        return jdbcTemplate.query(sql, teamRowMapper, "%" + coach + "%");
    }

    public List<Team> findByCityOrderByNameAsc(String city) {
        String sql = "SELECT * FROM teams WHERE city = ? ORDER BY name ASC";
        return jdbcTemplate.query(sql, teamRowMapper, city);
    }

    public List<Team> findAllByOrderByNameAsc() {
        String sql = "SELECT * FROM teams ORDER BY name ASC";
        return jdbcTemplate.query(sql, teamRowMapper);
    }

    public List<Team> findAllByOrderByFoundedYearDesc() {
        String sql = "SELECT * FROM teams ORDER BY founded_year DESC";
        return jdbcTemplate.query(sql, teamRowMapper);
    }

    public Optional<Team> findByIdWithMatches(UUID teamId) {
        return findById(teamId);
    }

    public List<Team> findTeamsWithUpcomingHomeMatches(LocalDateTime now) {
        String sql = "SELECT DISTINCT t.* FROM teams t JOIN matches m ON t.id = m.home_team_id WHERE m.match_date > ?";
        return jdbcTemplate.query(sql, teamRowMapper, Timestamp.valueOf(now));
    }

    public List<Team> findTeamsWithUpcomingAwayMatches(LocalDateTime now) {
        String sql = "SELECT DISTINCT t.* FROM teams t JOIN matches m ON t.id = m.away_team_id WHERE m.match_date > ?";
        return jdbcTemplate.query(sql, teamRowMapper, Timestamp.valueOf(now));
    }

    public List<Team> findTeamsWithHomeMatchesInFuture(LocalDateTime now) {
        return findTeamsWithUpcomingHomeMatches(now);
    }

    public List<Team> findTeamsWithAwayMatchesInFuture(LocalDateTime now) {
        return findTeamsWithUpcomingAwayMatches(now);
    }

    public List<Team> findTeamsWithAnyMatch() {
        String sql = "SELECT * FROM teams WHERE id IN (SELECT home_team_id FROM matches WHERE home_team_id IS NOT NULL UNION SELECT away_team_id FROM matches WHERE away_team_id IS NOT NULL)";
        return jdbcTemplate.query(sql, teamRowMapper);
    }

    public List<Team> findTeamsWithoutMatches() {
        String sql = "SELECT * FROM teams WHERE id NOT IN (SELECT home_team_id FROM matches WHERE home_team_id IS NOT NULL UNION SELECT away_team_id FROM matches WHERE away_team_id IS NOT NULL)";
        return jdbcTemplate.query(sql, teamRowMapper);
    }

    public long countUpcomingHomeMatchesByTeamId(UUID teamId, LocalDateTime now) {
        String sql = "SELECT COUNT(id) FROM matches WHERE home_team_id = ? AND match_date > ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, teamId, Timestamp.valueOf(now));
        return count != null ? count : 0L;
    }

    public long countUpcomingAwayMatchesByTeamId(UUID teamId, LocalDateTime now) {
        String sql = "SELECT COUNT(id) FROM matches WHERE away_team_id = ? AND match_date > ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, teamId, Timestamp.valueOf(now));
        return count != null ? count : 0L;
    }

    public long countAllHomeMatchesByTeamId(UUID teamId) {
        String sql = "SELECT COUNT(id) FROM matches WHERE home_team_id = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, teamId);
        return count != null ? count : 0L;
    }

    public long countAllAwayMatchesByTeamId(UUID teamId) {
        String sql = "SELECT COUNT(id) FROM matches WHERE away_team_id = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, teamId);
        return count != null ? count : 0L;
    }

    public List<Object[]> countAllMatchesByTeam() {
        String sql = "SELECT t.id, (SELECT COUNT(id) FROM matches WHERE home_team_id = t.id) + (SELECT COUNT(id) FROM matches WHERE away_team_id = t.id) as total FROM teams t ORDER BY total DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Object[]{rs.getObject(1), rs.getLong(2)});
    }

    public List<Object[]> countHomeMatchesByTeam() {
        String sql = "SELECT t.id, COUNT(m.id) FROM teams t LEFT JOIN matches m ON t.id = m.home_team_id GROUP BY t.id ORDER BY COUNT(m.id) DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Object[]{rs.getObject(1), rs.getLong(2)});
    }

    public List<Object[]> countAwayMatchesByTeam() {
        String sql = "SELECT t.id, COUNT(m.id) FROM teams t LEFT JOIN matches m ON t.id = m.away_team_id GROUP BY t.id ORDER BY COUNT(m.id) DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Object[]{rs.getObject(1), rs.getLong(2)});
    }

    public List<Team> findWithFoundedYear() {
        String sql = "SELECT * FROM teams WHERE founded_year IS NOT NULL";
        return jdbcTemplate.query(sql, teamRowMapper);
    }

    public List<Team> findWithoutFoundedYear() {
        String sql = "SELECT * FROM teams WHERE founded_year IS NULL";
        return jdbcTemplate.query(sql, teamRowMapper);
    }

    public List<Team> findWithLogo() {
        String sql = "SELECT * FROM teams WHERE logo IS NOT NULL AND logo != ''";
        return jdbcTemplate.query(sql, teamRowMapper);
    }

    public List<Team> findWithoutLogo() {
        String sql = "SELECT * FROM teams WHERE logo IS NULL OR logo = ''";
        return jdbcTemplate.query(sql, teamRowMapper);
    }

    public List<Team> findWithDescription() {
        String sql = "SELECT * FROM teams WHERE description IS NOT NULL AND description != ''";
        return jdbcTemplate.query(sql, teamRowMapper);
    }

    public List<Team> findWithCoach() {
        String sql = "SELECT * FROM teams WHERE coach IS NOT NULL AND coach != ''";
        return jdbcTemplate.query(sql, teamRowMapper);
    }

    public List<Team> findTeamsWithAvailableTickets() {
        String sql = "SELECT DISTINCT t.* FROM teams t JOIN matches m ON t.id = m.home_team_id JOIN ticket_categories tc ON m.id = tc.match_id WHERE tc.remaining_capacity > 0";
        return jdbcTemplate.query(sql, teamRowMapper);
    }

    public List<Team> findTeamsWithUpcomingMatches(LocalDateTime now) {
        String sql = "SELECT DISTINCT t.* FROM teams t JOIN matches m ON (t.id = m.home_team_id OR t.id = m.away_team_id) WHERE m.match_date > ?";
        return jdbcTemplate.query(sql, teamRowMapper, Timestamp.valueOf(now));
    }

    public List<Team> findTeamsWithFinishedMatches(LocalDateTime now) {
        String sql = "SELECT DISTINCT t.* FROM teams t JOIN matches m ON (t.id = m.home_team_id OR t.id = m.away_team_id) WHERE m.match_date < ?";
        return jdbcTemplate.query(sql, teamRowMapper, Timestamp.valueOf(now));
    }

    public List<Object[]> countTeamsByCity() {
        String sql = "SELECT city, COUNT(id) FROM teams GROUP BY city";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Object[]{rs.getString(1), rs.getLong(2)});
    }

    public Double averageFoundedYear() {
        String sql = "SELECT AVG(founded_year) FROM teams WHERE founded_year IS NOT NULL";
        return jdbcTemplate.queryForObject(sql, Double.class);
    }

    public Integer findLatestFoundedYear() {
        String sql = "SELECT MAX(founded_year) FROM teams";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public Integer findEarliestFoundedYear() {
        String sql = "SELECT MIN(founded_year) FROM teams";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public boolean existsByNameIgnoreCase(String name) {
        String sql = "SELECT COUNT(id) FROM teams WHERE LOWER(name) = LOWER(?)";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, name);
        return count != null && count > 0;
    }

    public boolean existsByNameAndCity(String name, String city) {
        String sql = "SELECT COUNT(id) FROM teams WHERE name = ? AND city = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, name, city);
        return count != null && count > 0;
    }
}