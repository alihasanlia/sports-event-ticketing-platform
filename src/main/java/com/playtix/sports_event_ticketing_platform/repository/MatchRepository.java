package com.playtix.sports_event_ticketing_platform.repository;

import com.playtix.sports_event_ticketing_platform.domain.entity.match.Match;
import com.playtix.sports_event_ticketing_platform.domain.entity.SportType;
import com.playtix.sports_event_ticketing_platform.domain.entity.Team;
import com.playtix.sports_event_ticketing_platform.domain.entity.Stadium;
import com.playtix.sports_event_ticketing_platform.domain.entity.League;
import com.playtix.sports_event_ticketing_platform.domain.entity.tournament.Tournament;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class MatchRepository {

    private final JdbcTemplate jdbcTemplate;

    public MatchRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final String SELECT_COLUMNS = "SELECT id, match_date, match_time, sport_type, home_team_id, away_team_id, stadium_id, league_id, tournament_id FROM matches";

    private final RowMapper<Match> matchRowMapper = (rs, rowNum) -> {
        Match match = new Match();
        match.setId((UUID) rs.getObject("id"));

        Timestamp matchDateTs = rs.getTimestamp("match_date");
        if (matchDateTs != null) {
            match.setMatchDate(matchDateTs.toLocalDateTime());
        }

        Timestamp matchTimeTs = rs.getTimestamp("match_time");
        if (matchTimeTs != null) {
            match.setMatchTime(matchTimeTs.toLocalDateTime());
        }

        String sportTypeStr = rs.getString("sport_type");
        if (sportTypeStr != null) {
            match.setSportType(SportType.valueOf(sportTypeStr));
        }

        UUID homeTeamId = (UUID) rs.getObject("home_team_id");
        if (homeTeamId != null) {
            Team homeTeam = new Team();
            homeTeam.setId(homeTeamId);
            match.setHomeTeam(homeTeam);
        }

        UUID awayTeamId = (UUID) rs.getObject("away_team_id");
        if (awayTeamId != null) {
            Team awayTeam = new Team();
            awayTeam.setId(awayTeamId);
            match.setAwayTeam(awayTeam);
        }

        UUID stadiumId = (UUID) rs.getObject("stadium_id");
        if (stadiumId != null) {
            Stadium stadium = new Stadium();
            stadium.setId(stadiumId);
            match.setStadium(stadium);
        }

        UUID leagueId = (UUID) rs.getObject("league_id");
        if (leagueId != null) {
            League league = new League();
            league.setId(leagueId);
            match.setLeague(league);
        }

        UUID tournamentId = (UUID) rs.getObject("tournament_id");
        if (tournamentId != null) {
            Tournament tournament = new Tournament();
            tournament.setId(tournamentId);
            match.setTournament(tournament);
        }

        return match;
    };

    public Optional<Match> findById(UUID id) {
        String sql = SELECT_COLUMNS + " WHERE id = ?";
        List<Match> results = jdbcTemplate.query(sql, matchRowMapper, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public void deleteById(UUID id) {
        String sql = "DELETE FROM matches WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public void delete(Match match) {
        if (match != null && match.getId() != null) {
            deleteById(match.getId());
        }
    }

    public Match save(Match match) {
        if (match.getId() == null) {
            match.setId(UUID.randomUUID());
            String sql = "INSERT INTO matches (id, match_date, match_time, sport_type, home_team_id, away_team_id, stadium_id, league_id, tournament_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(sql,
                match.getId(),
                match.getMatchDate(),
                match.getMatchTime(),
                match.getSportType() != null ? match.getSportType().name() : null,
                match.getHomeTeam() != null ? match.getHomeTeam().getId() : null,
                match.getAwayTeam() != null ? match.getAwayTeam().getId() : null,
                match.getStadium() != null ? match.getStadium().getId() : null,
                match.getLeague() != null ? match.getLeague().getId() : null,
                match.getTournament() != null ? match.getTournament().getId() : null
            );
        } else {
            String sql = "UPDATE matches SET match_date = ?, match_time = ?, sport_type = ?, home_team_id = ?, away_team_id = ?, stadium_id = ?, league_id = ?, tournament_id = ? WHERE id = ?";
            jdbcTemplate.update(sql,
                match.getMatchDate(),
                match.getMatchTime(),
                match.getSportType() != null ? match.getSportType().name() : null,
                match.getHomeTeam() != null ? match.getHomeTeam().getId() : null,
                match.getAwayTeam() != null ? match.getAwayTeam().getId() : null,
                match.getStadium() != null ? match.getStadium().getId() : null,
                match.getLeague() != null ? match.getLeague().getId() : null,
                match.getTournament() != null ? match.getTournament().getId() : null,
                match.getId()
            );
        }
        return match;
    }

    public List<Match> findAll() {
        return jdbcTemplate.query(SELECT_COLUMNS, matchRowMapper);
    }

    public List<Match> findByHomeTeam_Id(UUID teamId) {
        String sql = SELECT_COLUMNS + " WHERE home_team_id = ?";
        return jdbcTemplate.query(sql, matchRowMapper, teamId);
    }

    public List<Match> findByAwayTeam_Id(UUID teamId) {
        String sql = SELECT_COLUMNS + " WHERE away_team_id = ?";
        return jdbcTemplate.query(sql, matchRowMapper, teamId);
    }

    public List<Match> findByStadium_Id(UUID stadiumId) {
        String sql = SELECT_COLUMNS + " WHERE stadium_id = ?";
        return jdbcTemplate.query(sql, matchRowMapper, stadiumId);
    }

    public List<Match> findByLeague_Id(UUID leagueId) {
        String sql = SELECT_COLUMNS + " WHERE league_id = ?";
        return jdbcTemplate.query(sql, matchRowMapper, leagueId);
    }

    public List<Match> findByTournament_Id(UUID tournamentId) {
        String sql = SELECT_COLUMNS + " WHERE tournament_id = ?";
        return jdbcTemplate.query(sql, matchRowMapper, tournamentId);
    }

    public List<Match> findBySportType(SportType sportType) {
        String sql = SELECT_COLUMNS + " WHERE sport_type = ?";
        return jdbcTemplate.query(sql, matchRowMapper, sportType != null ? sportType.name() : null);
    }

    public List<Match> findByMatchDateBetween(LocalDateTime start, LocalDateTime end) {
        String sql = SELECT_COLUMNS + " WHERE match_date BETWEEN ? AND ?";
        return jdbcTemplate.query(sql, matchRowMapper, start, end);
    }

    public List<Match> findByMatchDateAfter(LocalDateTime date) {
        String sql = SELECT_COLUMNS + " WHERE match_date > ?";
        return jdbcTemplate.query(sql, matchRowMapper, date);
    }

    public List<Match> findByMatchDateBefore(LocalDateTime date) {
        String sql = SELECT_COLUMNS + " WHERE match_date < ?";
        return jdbcTemplate.query(sql, matchRowMapper, date);
    }

    public List<Match> findByMatchTimeAfter(LocalDateTime time) {
        String sql = SELECT_COLUMNS + " WHERE match_time > ?";
        return jdbcTemplate.query(sql, matchRowMapper, time);
    }

    public List<Match> findByMatchTimeBefore(LocalDateTime time) {
        String sql = SELECT_COLUMNS + " WHERE match_time < ?";
        return jdbcTemplate.query(sql, matchRowMapper, time);
    }

    public List<Match> findByHomeTeam_IdOrAwayTeam_Id(UUID teamId1, UUID teamId2) {
        String sql = SELECT_COLUMNS + " WHERE home_team_id = ? OR away_team_id = ?";
        return jdbcTemplate.query(sql, matchRowMapper, teamId1, teamId2);
    }

    public List<Match> findByMatchDateBetweenOrderByMatchDateAsc(LocalDateTime start, LocalDateTime end) {
        String sql = SELECT_COLUMNS + " WHERE match_date BETWEEN ? AND ? ORDER BY match_date ASC";
        return jdbcTemplate.query(sql, matchRowMapper, start, end);
    }

    public List<Match> findByMatchDateAfterOrderByMatchDateAsc(LocalDateTime date) {
        String sql = SELECT_COLUMNS + " WHERE match_date > ? ORDER BY match_date ASC";
        return jdbcTemplate.query(sql, matchRowMapper, date);
    }

    public List<Match> findByMatchDateBeforeOrderByMatchDateDesc(LocalDateTime date) {
        String sql = SELECT_COLUMNS + " WHERE match_date < ? ORDER BY match_date DESC";
        return jdbcTemplate.query(sql, matchRowMapper, date);
    }

    public List<Match> findByHomeTeam_NameContainingIgnoreCase(String name) {
        String sql = "SELECT m.id, m.match_date, m.match_time, m.sport_type, m.home_team_id, m.away_team_id, m.stadium_id, m.league_id, m.tournament_id FROM matches m JOIN teams t ON m.home_team_id = t.id WHERE LOWER(t.name) LIKE LOWER(?)";
        return jdbcTemplate.query(sql, matchRowMapper, "%" + name + "%");
    }

    public List<Match> findByAwayTeam_NameContainingIgnoreCase(String name) {
        String sql = "SELECT m.id, m.match_date, m.match_time, m.sport_type, m.home_team_id, m.away_team_id, m.stadium_id, m.league_id, m.tournament_id FROM matches m JOIN teams t ON m.away_team_id = t.id WHERE LOWER(t.name) LIKE LOWER(?)";
        return jdbcTemplate.query(sql, matchRowMapper, "%" + name + "%");
    }

    public Optional<Match> findByIdWithAllRelationships(UUID matchId) {
        return findById(matchId);
    }

    public Optional<Match> findByIdWithTeamsAndStadium(UUID matchId) {
        return findById(matchId);
    }

    public Optional<Match> findByIdWithTicketCategories(UUID matchId) {
        return findById(matchId);
    }

    public Optional<Match> findByIdWithTeams(UUID matchId) {
        return findById(matchId);
    }

    public List<Match> findMatchesWithCategoriesInDateRange(LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT DISTINCT m.id, m.match_date, m.match_time, m.sport_type, m.home_team_id, m.away_team_id, m.stadium_id, m.league_id, m.tournament_id FROM matches m JOIN ticket_categories tc ON m.id = tc.match_id WHERE m.match_date BETWEEN ? AND ?";
        return jdbcTemplate.query(sql, matchRowMapper, start, end);
    }

    public List<Match> findUpcomingMatches(LocalDateTime now) {
        String sql = SELECT_COLUMNS + " WHERE match_date > ? ORDER BY match_date ASC";
        return jdbcTemplate.query(sql, matchRowMapper, now);
    }

    public List<Match> findFinishedMatches(LocalDateTime now) {
        String sql = SELECT_COLUMNS + " WHERE match_date < ? ORDER BY match_date DESC";
        return jdbcTemplate.query(sql, matchRowMapper, now);
    }

    public List<Match> findByStadiumAndDateRange(UUID stadiumId, LocalDateTime start, LocalDateTime end) {
        String sql = SELECT_COLUMNS + " WHERE stadium_id = ? AND match_date BETWEEN ? AND ?";
        return jdbcTemplate.query(sql, matchRowMapper, stadiumId, start, end);
    }

    public List<Match> findUpcomingMatchesByTeamId(UUID teamId, LocalDateTime now) {
        String sql = SELECT_COLUMNS + " WHERE (home_team_id = ? OR away_team_id = ?) AND match_date > ? ORDER BY match_date ASC";
        return jdbcTemplate.query(sql, matchRowMapper, teamId, teamId, now);
    }

    public List<Match> findFinishedMatchesByTeamId(UUID teamId, LocalDateTime now) {
        String sql = SELECT_COLUMNS + " WHERE (home_team_id = ? OR away_team_id = ?) AND match_date < ? ORDER BY match_date DESC";
        return jdbcTemplate.query(sql, matchRowMapper, teamId, teamId, now);
    }

    public List<Match> findByHomeTeamIdAndAwayTeamId(UUID homeTeamId, UUID awayTeamId) {
        String sql = SELECT_COLUMNS + " WHERE home_team_id = ? AND away_team_id = ?";
        return jdbcTemplate.query(sql, matchRowMapper, homeTeamId, awayTeamId);
    }

    public List<Match> findUpcomingMatchesBetweenTeams(UUID homeTeamId, UUID awayTeamId, LocalDateTime now) {
        String sql = SELECT_COLUMNS + " WHERE home_team_id = ? AND away_team_id = ? AND match_date > ?";
        return jdbcTemplate.query(sql, matchRowMapper, homeTeamId, awayTeamId, now);
    }

    public long countMatchesByStadiumAndDateRange(UUID stadiumId, LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT COUNT(*) FROM matches WHERE stadium_id = ? AND match_date BETWEEN ? AND ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, stadiumId, start, end);
        return count != null ? count : 0L;
    }

    public long countUpcomingMatchesByLeagueId(UUID leagueId, LocalDateTime now) {
        String sql = "SELECT COUNT(*) FROM matches WHERE league_id = ? AND match_date > ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, leagueId, now);
        return count != null ? count : 0L;
    }

    public long countUpcomingMatchesByTournamentId(UUID tournamentId, LocalDateTime now) {
        String sql = "SELECT COUNT(*) FROM matches WHERE tournament_id = ? AND match_date > ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, tournamentId, now);
        return count != null ? count : 0L;
    }

    public List<Object[]> countMatchesByStadiumInDateRange(LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT stadium_id, COUNT(*) FROM matches WHERE match_date BETWEEN ? AND ? GROUP BY stadium_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Object[] { rs.getObject(1), rs.getLong(2) }, start, end);
    }

    public List<Object[]> countUpcomingMatchesBySportType(LocalDateTime now) {
        String sql = "SELECT sport_type, COUNT(*) FROM matches WHERE match_date > ? GROUP BY sport_type";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Object[] { rs.getString(1), rs.getLong(2) }, now);
    }

    public List<Object[]> countMatchesByLeagueInDateRange(LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT league_id, COUNT(*) FROM matches WHERE match_date BETWEEN ? AND ? GROUP BY league_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Object[] { rs.getObject(1), rs.getLong(2) }, start, end);
    }

    public List<Match> findUpcomingMatchesByStadiumId(UUID stadiumId, LocalDateTime now) {
        String sql = SELECT_COLUMNS + " WHERE stadium_id = ? AND match_date > ? ORDER BY match_date ASC";
        return jdbcTemplate.query(sql, matchRowMapper, stadiumId, now);
    }

    public List<Match> findFinishedMatchesByStadiumId(UUID stadiumId, LocalDateTime now) {
        String sql = SELECT_COLUMNS + " WHERE stadium_id = ? AND match_date < ? ORDER BY match_date DESC";
        return jdbcTemplate.query(sql, matchRowMapper, stadiumId, now);
    }

    public List<Match> findByLeagueIdOrderByMatchDateDesc(UUID leagueId) {
        String sql = SELECT_COLUMNS + " WHERE league_id = ? ORDER BY match_date DESC";
        return jdbcTemplate.query(sql, matchRowMapper, leagueId);
    }

    public List<Match> findByTournamentIdOrderByMatchDateDesc(UUID tournamentId) {
        String sql = SELECT_COLUMNS + " WHERE tournament_id = ? ORDER BY match_date DESC";
        return jdbcTemplate.query(sql, matchRowMapper, tournamentId);
    }

    public List<Match> findUpcomingMatchesBySportType(SportType sportType, LocalDateTime now) {
        String sql = SELECT_COLUMNS + " WHERE sport_type = ? AND match_date > ? ORDER BY match_date ASC";
        return jdbcTemplate.query(sql, matchRowMapper, sportType != null ? sportType.name() : null, now);
    }

    public List<Match> findByTeamNameContaining(String teamName) {
        String sql = "SELECT DISTINCT m.id, m.match_date, m.match_time, m.sport_type, m.home_team_id, m.away_team_id, m.stadium_id, m.league_id, m.tournament_id FROM matches m JOIN teams t1 ON m.home_team_id = t1.id JOIN teams t2 ON m.away_team_id = t2.id WHERE t1.name LIKE ? OR t2.name LIKE ?";
        String pattern = "%" + teamName + "%";
        return jdbcTemplate.query(sql, matchRowMapper, pattern, pattern);
    }

    public List<Match> findByDateRangeAndSportType(LocalDateTime start, LocalDateTime end, SportType sportType) {
        String sql = SELECT_COLUMNS + " WHERE match_date BETWEEN ? AND ? AND sport_type = ?";
        return jdbcTemplate.query(sql, matchRowMapper, start, end, sportType != null ? sportType.name() : null);
    }

    public List<Match> findByMatchDate(LocalDateTime date) {
        String sql = SELECT_COLUMNS + " WHERE CAST(match_date AS DATE) = CAST(? AS DATE)";
        return jdbcTemplate.query(sql, matchRowMapper, date);
    }

    public boolean existsByHomeTeam_IdAndMatchDateAfter(UUID teamId, LocalDateTime now) {
        String sql = "SELECT COUNT(*) FROM matches WHERE home_team_id = ? AND match_date > ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, teamId, now);
        return count != null && count > 0;
    }

    public boolean existsByAwayTeam_IdAndMatchDateAfter(UUID teamId, LocalDateTime now) {
        String sql = "SELECT COUNT(*) FROM matches WHERE away_team_id = ? AND match_date > ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, teamId, now);
        return count != null && count > 0;
    }

    public boolean existsUpcomingMatchBetweenTeams(UUID homeTeamId, UUID awayTeamId, LocalDateTime now) {
        String sql = "SELECT COUNT(*) FROM matches WHERE home_team_id = ? AND away_team_id = ? AND match_date > ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, homeTeamId, awayTeamId, now);
        return count != null && count > 0;
    }
}