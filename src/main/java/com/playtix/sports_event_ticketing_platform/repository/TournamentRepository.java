package com.playtix.sports_event_ticketing_platform.repository;

import com.playtix.sports_event_ticketing_platform.domain.entity.Sport;
import com.playtix.sports_event_ticketing_platform.domain.entity.tournament.Tournament;
import com.playtix.sports_event_ticketing_platform.domain.entity.tournament.TournamentStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TournamentRepository {

    private final JdbcTemplate jdbcTemplate;

    public TournamentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Tournament> tournamentRowMapper = (rs, rowNum) -> {
        Tournament tournament = new Tournament();
        tournament.setId(UUID.fromString(rs.getString("id")));
        tournament.setName(rs.getString("name"));
        tournament.setDescription(rs.getString("description"));
        
        Timestamp startDate = rs.getTimestamp("start_date");
        if (startDate != null) {
            tournament.setStartDate(startDate.toLocalDateTime());
        }
        
        Timestamp endDate = rs.getTimestamp("end_date");
        if (endDate != null) {
            tournament.setEndDate(endDate.toLocalDateTime());
        }
        
        String statusStr = rs.getString("status");
        if (statusStr != null) {
            tournament.setStatus(TournamentStatus.valueOf(statusStr));
        }
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            tournament.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            tournament.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        String sportIdStr = rs.getString("sport_id");
        if (sportIdStr != null) {
            Sport sport = new Sport();
            sport.setId(UUID.fromString(sportIdStr));
            tournament.setSport(sport);
        }
        
        return tournament;
    };

    // --- Standard CRUD Methods ---

    public List<Tournament> findAll() {
        String sql = "SELECT * FROM tournaments";
        return jdbcTemplate.query(sql, tournamentRowMapper);
    }

    public Optional<Tournament> findById(UUID id) {
        String sql = "SELECT * FROM tournaments WHERE id = ?";
        List<Tournament> results = jdbcTemplate.query(sql, tournamentRowMapper, id);
        return results.stream().findFirst();
    }

    public Tournament save(Tournament tournament) {
        if (tournament.getId() != null && findById(tournament.getId()).isPresent()) {
            String sql = "UPDATE tournaments SET name = ?, description = ?, start_date = ?, end_date = ?, status = ?, updated_at = ?, sport_id = ? WHERE id = ?";
            if (tournament.getUpdatedAt() == null) {
                tournament.setUpdatedAt(LocalDateTime.now());
            }
            jdbcTemplate.update(sql,
                    tournament.getName(),
                    tournament.getDescription(),
                    tournament.getStartDate() != null ? Timestamp.valueOf(tournament.getStartDate()) : null,
                    tournament.getEndDate() != null ? Timestamp.valueOf(tournament.getEndDate()) : null,
                    tournament.getStatus() != null ? tournament.getStatus().name() : null,
                    Timestamp.valueOf(tournament.getUpdatedAt()),
                    tournament.getSport() != null ? tournament.getSport().getId() : null,
                    tournament.getId()
            );
        } else {
            String sql = "INSERT INTO tournaments (id, name, description, start_date, end_date, status, created_at, updated_at, sport_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            if (tournament.getId() == null) {
                tournament.setId(UUID.randomUUID());
            }
            if (tournament.getCreatedAt() == null) {
                tournament.setCreatedAt(LocalDateTime.now());
            }
            if (tournament.getStatus() == null) {
                tournament.setStatus(TournamentStatus.UPCOMING);
            }
            jdbcTemplate.update(sql,
                    tournament.getId(),
                    tournament.getName(),
                    tournament.getDescription(),
                    tournament.getStartDate() != null ? Timestamp.valueOf(tournament.getStartDate()) : null,
                    tournament.getEndDate() != null ? Timestamp.valueOf(tournament.getEndDate()) : null,
                    tournament.getStatus().name(),
                    Timestamp.valueOf(tournament.getCreatedAt()),
                    tournament.getUpdatedAt() != null ? Timestamp.valueOf(tournament.getUpdatedAt()) : null,
                    tournament.getSport() != null ? tournament.getSport().getId() : null
            );
        }
        return tournament;
    }

    public void deleteById(UUID id) {
        String sql = "DELETE FROM tournaments WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public void delete(Tournament tournament) {
        if (tournament != null && tournament.getId() != null) {
            deleteById(tournament.getId());
        }
    }

    // --- Interface Specific Query Methods ---

    public Optional<Tournament> findByName(String name) {
        String sql = "SELECT * FROM tournaments WHERE name = ?";
        List<Tournament> results = jdbcTemplate.query(sql, tournamentRowMapper, name);
        return results.stream().findFirst();
    }

    public List<Tournament> findByNameContainingIgnoreCase(String name) {
        String sql = "SELECT * FROM tournaments WHERE LOWER(name) LIKE LOWER(?)";
        return jdbcTemplate.query(sql, tournamentRowMapper, "%" + name + "%");
    }

    public List<Tournament> findByStatus(TournamentStatus status) {
        String sql = "SELECT * FROM tournaments WHERE status = ?";
        return jdbcTemplate.query(sql, tournamentRowMapper, status.name());
    }

    public List<Tournament> findBySport_Id(UUID sportId) {
        String sql = "SELECT * FROM tournaments WHERE sport_id = ?";
        return jdbcTemplate.query(sql, tournamentRowMapper, sportId);
    }

    public List<Tournament> findBySport_IdAndStatus(UUID sportId, TournamentStatus status) {
        String sql = "SELECT * FROM tournaments WHERE sport_id = ? AND status = ?";
        return jdbcTemplate.query(sql, tournamentRowMapper, sportId, status.name());
    }

    public List<Tournament> findByStartDateBetween(LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT * FROM tournaments WHERE start_date BETWEEN ? AND ?";
        return jdbcTemplate.query(sql, tournamentRowMapper, Timestamp.valueOf(start), Timestamp.valueOf(end));
    }

    public List<Tournament> findByEndDateBetween(LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT * FROM tournaments WHERE end_date BETWEEN ? AND ?";
        return jdbcTemplate.query(sql, tournamentRowMapper, Timestamp.valueOf(start), Timestamp.valueOf(end));
    }

    public List<Tournament> findByStartDateAfter(LocalDateTime date) {
        String sql = "SELECT * FROM tournaments WHERE start_date > ?";
        return jdbcTemplate.query(sql, tournamentRowMapper, Timestamp.valueOf(date));
    }

    public List<Tournament> findByStartDateBefore(LocalDateTime date) {
        String sql = "SELECT * FROM tournaments WHERE start_date < ?";
        return jdbcTemplate.query(sql, tournamentRowMapper, Timestamp.valueOf(date));
    }

    public List<Tournament> findByEndDateAfter(LocalDateTime date) {
        String sql = "SELECT * FROM tournaments WHERE end_date > ?";
        return jdbcTemplate.query(sql, tournamentRowMapper, Timestamp.valueOf(date));
    }

    public List<Tournament> findByEndDateBefore(LocalDateTime date) {
        String sql = "SELECT * FROM tournaments WHERE end_date < ?";
        return jdbcTemplate.query(sql, tournamentRowMapper, Timestamp.valueOf(date));
    }

    public List<Tournament> findByStartDateIsNull() {
        String sql = "SELECT * FROM tournaments WHERE start_date IS NULL";
        return jdbcTemplate.query(sql, tournamentRowMapper);
    }

    public List<Tournament> findByEndDateIsNull() {
        String sql = "SELECT * FROM tournaments WHERE end_date IS NULL";
        return jdbcTemplate.query(sql, tournamentRowMapper);
    }

    public List<Tournament> findByStatusOrderByStartDateAsc(TournamentStatus status) {
        String sql = "SELECT * FROM tournaments WHERE status = ? ORDER BY start_date ASC";
        return jdbcTemplate.query(sql, tournamentRowMapper, status.name());
    }

    public List<Tournament> findAllByOrderByStartDateDesc() {
        String sql = "SELECT * FROM tournaments ORDER BY start_date DESC";
        return jdbcTemplate.query(sql, tournamentRowMapper);
    }

    public List<Tournament> findAllByOrderByNameAsc() {
        String sql = "SELECT * FROM tournaments ORDER BY name ASC";
        return jdbcTemplate.query(sql, tournamentRowMapper);
    }

    public Optional<Tournament> findByIdWithMatchesAndSport(UUID tournamentId) {
        return findById(tournamentId);
    }

    public Optional<Tournament> findByIdWithMatches(UUID tournamentId) {
        return findById(tournamentId);
    }

    public Optional<Tournament> findByIdWithSport(UUID tournamentId) {
        return findById(tournamentId);
    }

    public List<Tournament> findUpcomingTournaments(LocalDateTime now) {
        String sql = "SELECT * FROM tournaments WHERE status = 'UPCOMING' AND start_date > ? ORDER BY start_date ASC";
        return jdbcTemplate.query(sql, tournamentRowMapper, Timestamp.valueOf(now));
    }

    public List<Tournament> findActiveTournaments(LocalDateTime now) {
        String sql = "SELECT * FROM tournaments WHERE status = 'ACTIVE' AND start_date <= ? AND (end_date IS NULL OR end_date >= ?)";
        Timestamp ts = Timestamp.valueOf(now);
        return jdbcTemplate.query(sql, tournamentRowMapper, ts, ts);
    }

    public List<Tournament> findFinishedTournaments(LocalDateTime now) {
        String sql = "SELECT * FROM tournaments WHERE status = 'FINISHED' OR (end_date IS NOT NULL AND end_date < ?)";
        return jdbcTemplate.query(sql, tournamentRowMapper, Timestamp.valueOf(now));
    }

    public List<Tournament> findStartedTournaments(LocalDateTime now) {
        String sql = "SELECT * FROM tournaments WHERE status = 'ACTIVE' OR (status = 'UPCOMING' AND start_date <= ?)";
        return jdbcTemplate.query(sql, tournamentRowMapper, Timestamp.valueOf(now));
    }

    public List<Tournament> findCancelledTournaments() {
        String sql = "SELECT * FROM tournaments WHERE status = 'CANCELLED'";
        return jdbcTemplate.query(sql, tournamentRowMapper);
    }

    public List<Tournament> findNotFinishedTournaments() {
        String sql = "SELECT * FROM tournaments WHERE status != 'FINISHED' AND status != 'CANCELLED'";
        return jdbcTemplate.query(sql, tournamentRowMapper);
    }

    public long countMatchesByTournamentId(UUID tournamentId) {
        String sql = "SELECT COUNT(id) FROM matches WHERE tournament_id = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, tournamentId);
        return count != null ? count : 0L;
    }

    public long countUpcomingMatchesByTournamentId(UUID tournamentId, LocalDateTime now) {
        String sql = "SELECT COUNT(id) FROM matches WHERE tournament_id = ? AND match_date > ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, tournamentId, Timestamp.valueOf(now));
        return count != null ? count : 0L;
    }

    public long countFinishedMatchesByTournamentId(UUID tournamentId, LocalDateTime now) {
        String sql = "SELECT COUNT(id) FROM matches WHERE tournament_id = ? AND match_date < ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, tournamentId, Timestamp.valueOf(now));
        return count != null ? count : 0L;
    }

    public List<Object[]> countMatchesByTournament() {
        String sql = "SELECT t.id, COUNT(m.id) FROM tournaments t LEFT JOIN matches m ON t.id = m.tournament_id GROUP BY t.id ORDER BY COUNT(m.id) DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Object[]{rs.getObject(1), rs.getLong(2)});
    }

    public List<Object[]> countTournamentsBySport() {
        String sql = "SELECT sport_id, COUNT(id) FROM tournaments GROUP BY sport_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Object[]{rs.getObject(1), rs.getLong(2)});
    }

    public List<Object[]> countTournamentsByStatus() {
        String sql = "SELECT status, COUNT(id) FROM tournaments GROUP BY status";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Object[]{rs.getString(1), rs.getLong(2)});
    }

    public List<Tournament> findWithDescription() {
        String sql = "SELECT * FROM tournaments WHERE description IS NOT NULL AND description != ''";
        return jdbcTemplate.query(sql, tournamentRowMapper);
    }

    public List<Tournament> findWithoutDescription() {
        String sql = "SELECT * FROM tournaments WHERE description IS NULL OR description = ''";
        return jdbcTemplate.query(sql, tournamentRowMapper);
    }

    public List<Tournament> findWithStartAndEndDate() {
        String sql = "SELECT * FROM tournaments WHERE start_date IS NOT NULL AND end_date IS NOT NULL";
        return jdbcTemplate.query(sql, tournamentRowMapper);
    }

    public List<Tournament> findWithoutStartOrEndDate() {
        String sql = "SELECT * FROM tournaments WHERE start_date IS NULL OR end_date IS NULL";
        return jdbcTemplate.query(sql, tournamentRowMapper);
    }

    public List<Tournament> findUpcomingTournamentsStartingAfter(LocalDateTime now) {
        String sql = "SELECT * FROM tournaments WHERE start_date >= ? AND status = 'UPCOMING'";
        return jdbcTemplate.query(sql, tournamentRowMapper, Timestamp.valueOf(now));
    }

    public List<Tournament> findTournamentsThatShouldBeFinished(LocalDateTime now) {
        String sql = "SELECT * FROM tournaments WHERE end_date <= ? AND status != 'FINISHED' AND status != 'CANCELLED'";
        return jdbcTemplate.query(sql, tournamentRowMapper, Timestamp.valueOf(now));
    }

    public List<Tournament> findActiveTournamentsBySportId(UUID sportId) {
        String sql = "SELECT * FROM tournaments WHERE sport_id = ? AND status = 'ACTIVE'";
        return jdbcTemplate.query(sql, tournamentRowMapper, sportId);
    }

    public List<Tournament> findUpcomingTournamentsBySportId(UUID sportId) {
        String sql = "SELECT * FROM tournaments WHERE sport_id = ? AND status = 'UPCOMING' ORDER BY start_date ASC";
        return jdbcTemplate.query(sql, tournamentRowMapper, sportId);
    }

    public List<Tournament> findFinishedTournamentsBySportId(UUID sportId) {
        String sql = "SELECT * FROM tournaments WHERE sport_id = ? AND status = 'FINISHED' ORDER BY end_date DESC";
        return jdbcTemplate.query(sql, tournamentRowMapper, sportId);
    }

    public List<Tournament> findTournamentsWithMatchesInDateRange(LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT DISTINCT t.* FROM tournaments t JOIN matches m ON t.id = m.tournament_id WHERE m.match_date BETWEEN ? AND ?";
        return jdbcTemplate.query(sql, tournamentRowMapper, Timestamp.valueOf(start), Timestamp.valueOf(end));
    }

    public List<Tournament> findTournamentsWithoutMatches() {
        String sql = "SELECT * FROM tournaments WHERE id NOT IN (SELECT DISTINCT tournament_id FROM matches WHERE tournament_id IS NOT NULL)";
        return jdbcTemplate.query(sql, tournamentRowMapper);
    }

    public List<Tournament> findDelayedTournaments(LocalDateTime now) {
        String sql = "SELECT * FROM tournaments WHERE status = 'UPCOMING' AND start_date IS NOT NULL AND start_date < ?";
        return jdbcTemplate.query(sql, tournamentRowMapper, Timestamp.valueOf(now));
    }

    public List<Tournament> findTournamentsByYear(int year) {
        String sql = "SELECT * FROM tournaments WHERE EXTRACT(YEAR FROM start_date) = ?";
        return jdbcTemplate.query(sql, tournamentRowMapper, year);
    }

    public boolean existsByNameIgnoreCase(String name) {
        String sql = "SELECT COUNT(id) FROM tournaments WHERE LOWER(name) = LOWER(?)";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, name);
        return count != null && count > 0;
    }

    public boolean existsByNameAndSportId(String name, UUID sportId) {
        String sql = "SELECT COUNT(id) FROM tournaments WHERE name = ? AND sport_id = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, name, sportId);
        return count != null && count > 0;
    }

    public List<Tournament> findLatestTournaments() {
        String sql = "SELECT * FROM tournaments WHERE start_date IS NOT NULL ORDER BY start_date DESC";
        return jdbcTemplate.query(sql, tournamentRowMapper);
    }
}