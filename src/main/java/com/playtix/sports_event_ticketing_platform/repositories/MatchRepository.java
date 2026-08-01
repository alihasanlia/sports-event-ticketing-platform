package com.playtix.sports_event_ticketing_platform.repositories;

import com.playtix.sports_event_ticketing_platform.domain.entity.match.Match;
import com.playtix.sports_event_ticketing_platform.domain.entity.SportType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MatchRepository extends JpaRepository<Match, UUID> {

    List<Match> findByHomeTeam_Id(UUID teamId);

    List<Match> findByAwayTeam_Id(UUID teamId);

    List<Match> findByStadium_Id(UUID stadiumId);

    List<Match> findByLeague_Id(UUID leagueId);

    List<Match> findByTournament_Id(UUID tournamentId);

    List<Match> findBySportType(SportType sportType);

    List<Match> findByMatchDateBetween(LocalDateTime start, LocalDateTime end);

    List<Match> findByMatchDateAfter(LocalDateTime date);

    List<Match> findByMatchDateBefore(LocalDateTime date);

    List<Match> findByMatchTimeAfter(LocalDateTime time);

    List<Match> findByMatchTimeBefore(LocalDateTime time);

    List<Match> findByHomeTeam_IdOrAwayTeam_Id(UUID teamId1, UUID teamId2);

    List<Match> findByMatchDateBetweenOrderByMatchDateAsc(LocalDateTime start, LocalDateTime end);

    List<Match> findByMatchDateAfterOrderByMatchDateAsc(LocalDateTime date);

    List<Match> findByMatchDateBeforeOrderByMatchDateDesc(LocalDateTime date);

    List<Match> findByHomeTeam_NameContainingIgnoreCase(String name);

    List<Match> findByAwayTeam_NameContainingIgnoreCase(String name);

    @Query("SELECT m FROM Match m LEFT JOIN FETCH m.homeTeam LEFT JOIN FETCH m.awayTeam LEFT JOIN FETCH m.stadium LEFT JOIN FETCH m.league LEFT JOIN FETCH m.tournament WHERE m.id = :matchId")
    Optional<Match> findByIdWithAllRelationships(@Param("matchId") UUID matchId);

    @Query("SELECT m FROM Match m LEFT JOIN FETCH m.homeTeam LEFT JOIN FETCH m.awayTeam LEFT JOIN FETCH m.stadium WHERE m.id = :matchId")
    Optional<Match> findByIdWithTeamsAndStadium(@Param("matchId") UUID matchId);

    @Query("SELECT m FROM Match m LEFT JOIN FETCH m.ticketCategories WHERE m.id = :matchId")
    Optional<Match> findByIdWithTicketCategories(@Param("matchId") UUID matchId);

    @Query("SELECT m FROM Match m LEFT JOIN FETCH m.homeTeam LEFT JOIN FETCH m.awayTeam WHERE m.id = :matchId")
    Optional<Match> findByIdWithTeams(@Param("matchId") UUID matchId);

    @Query("SELECT m FROM Match m LEFT JOIN FETCH m.ticketCategories WHERE m.matchDate BETWEEN :start AND :end")
    List<Match> findMatchesWithCategoriesInDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT m FROM Match m WHERE m.matchDate > :now ORDER BY m.matchDate ASC")
    List<Match> findUpcomingMatches(@Param("now") LocalDateTime now);

    @Query("SELECT m FROM Match m WHERE m.matchDate < :now ORDER BY m.matchDate DESC")
    List<Match> findFinishedMatches(@Param("now") LocalDateTime now);

    @Query("SELECT m FROM Match m WHERE m.matchDate BETWEEN :start AND :end AND m.stadium.id = :stadiumId")
    List<Match> findByStadiumAndDateRange(@Param("stadiumId") UUID stadiumId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT m FROM Match m WHERE (m.homeTeam.id = :teamId OR m.awayTeam.id = :teamId) AND m.matchDate > :now ORDER BY m.matchDate ASC")
    List<Match> findUpcomingMatchesByTeamId(@Param("teamId") UUID teamId, @Param("now") LocalDateTime now);

    @Query("SELECT m FROM Match m WHERE (m.homeTeam.id = :teamId OR m.awayTeam.id = :teamId) AND m.matchDate < :now ORDER BY m.matchDate DESC")
    List<Match> findFinishedMatchesByTeamId(@Param("teamId") UUID teamId, @Param("now") LocalDateTime now);

    @Query("SELECT m FROM Match m WHERE m.homeTeam.id = :homeTeamId AND m.awayTeam.id = :awayTeamId")
    List<Match> findByHomeTeamIdAndAwayTeamId(@Param("homeTeamId") UUID homeTeamId, @Param("awayTeamId") UUID awayTeamId);

    @Query("SELECT m FROM Match m WHERE m.homeTeam.id = :homeTeamId AND m.awayTeam.id = :awayTeamId AND m.matchDate > :now")
    List<Match> findUpcomingMatchesBetweenTeams(@Param("homeTeamId") UUID homeTeamId, @Param("awayTeamId") UUID awayTeamId, @Param("now") LocalDateTime now);

    @Query("SELECT COUNT(m) FROM Match m WHERE m.stadium.id = :stadiumId AND m.matchDate BETWEEN :start AND :end")
    long countMatchesByStadiumAndDateRange(@Param("stadiumId") UUID stadiumId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(m) FROM Match m WHERE m.league.id = :leagueId AND m.matchDate > :now")
    long countUpcomingMatchesByLeagueId(@Param("leagueId") UUID leagueId, @Param("now") LocalDateTime now);

    @Query("SELECT COUNT(m) FROM Match m WHERE m.tournament.id = :tournamentId AND m.matchDate > :now")
    long countUpcomingMatchesByTournamentId(@Param("tournamentId") UUID tournamentId, @Param("now") LocalDateTime now);

    @Query("SELECT m.stadium.id, COUNT(m) FROM Match m WHERE m.matchDate BETWEEN :start AND :end GROUP BY m.stadium.id")
    List<Object[]> countMatchesByStadiumInDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT m.sportType, COUNT(m) FROM Match m WHERE m.matchDate > :now GROUP BY m.sportType")
    List<Object[]> countUpcomingMatchesBySportType(@Param("now") LocalDateTime now);

    @Query("SELECT m.league.id, COUNT(m) FROM Match m WHERE m.matchDate BETWEEN :start AND :end GROUP BY m.league.id")
    List<Object[]> countMatchesByLeagueInDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT m FROM Match m WHERE m.matchDate > :now AND m.stadium.id = :stadiumId ORDER BY m.matchDate ASC")
    List<Match> findUpcomingMatchesByStadiumId(@Param("stadiumId") UUID stadiumId, @Param("now") LocalDateTime now);

    @Query("SELECT m FROM Match m WHERE m.matchDate < :now AND m.stadium.id = :stadiumId ORDER BY m.matchDate DESC")
    List<Match> findFinishedMatchesByStadiumId(@Param("stadiumId") UUID stadiumId, @Param("now") LocalDateTime now);

    @Query("SELECT m FROM Match m WHERE m.league.id = :leagueId ORDER BY m.matchDate DESC")
    List<Match> findByLeagueIdOrderByMatchDateDesc(@Param("leagueId") UUID leagueId);

    @Query("SELECT m FROM Match m WHERE m.tournament.id = :tournamentId ORDER BY m.matchDate DESC")
    List<Match> findByTournamentIdOrderByMatchDateDesc(@Param("tournamentId") UUID tournamentId);

    @Query("SELECT m FROM Match m WHERE m.sportType = :sportType AND m.matchDate > :now ORDER BY m.matchDate ASC")
    List<Match> findUpcomingMatchesBySportType(@Param("sportType") SportType sportType, @Param("now") LocalDateTime now);

    @Query("SELECT m FROM Match m WHERE m.homeTeam.name LIKE %:teamName% OR m.awayTeam.name LIKE %:teamName%")
    List<Match> findByTeamNameContaining(@Param("teamName") String teamName);

    @Query("SELECT m FROM Match m WHERE m.matchDate BETWEEN :start AND :end AND m.sportType = :sportType")
    List<Match> findByDateRangeAndSportType(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("sportType") SportType sportType);

    @Query("SELECT m FROM Match m WHERE FUNCTION('DATE', m.matchDate) = FUNCTION('DATE', :date)")
    List<Match> findByMatchDate(@Param("date") LocalDateTime date);

    boolean existsByHomeTeam_IdAndMatchDateAfter(UUID teamId, LocalDateTime now);

    boolean existsByAwayTeam_IdAndMatchDateAfter(UUID teamId, LocalDateTime now);

    @Query("SELECT COUNT(m) > 0 FROM Match m WHERE m.homeTeam.id = :homeTeamId AND m.awayTeam.id = :awayTeamId AND m.matchDate > :now")
    boolean existsUpcomingMatchBetweenTeams(@Param("homeTeamId") UUID homeTeamId, @Param("awayTeamId") UUID awayTeamId, @Param("now") LocalDateTime now);
}