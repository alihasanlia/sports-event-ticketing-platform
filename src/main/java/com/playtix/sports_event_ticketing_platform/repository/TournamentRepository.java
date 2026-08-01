package com.playtix.sports_event_ticketing_platform.repository;

import com.playtix.sports_event_ticketing_platform.domain.entity.tournament.Tournament;
import com.playtix.sports_event_ticketing_platform.domain.entity.tournament.TournamentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TournamentRepository extends JpaRepository<Tournament, UUID> {

    Optional<Tournament> findByName(String name);

    List<Tournament> findByNameContainingIgnoreCase(String name);

    List<Tournament> findByStatus(TournamentStatus status);

    List<Tournament> findBySport_Id(UUID sportId);

    List<Tournament> findBySport_IdAndStatus(UUID sportId, TournamentStatus status);

    List<Tournament> findByStartDateBetween(LocalDateTime start, LocalDateTime end);

    List<Tournament> findByEndDateBetween(LocalDateTime start, LocalDateTime end);

    List<Tournament> findByStartDateAfter(LocalDateTime date);

    List<Tournament> findByStartDateBefore(LocalDateTime date);

    List<Tournament> findByEndDateAfter(LocalDateTime date);

    List<Tournament> findByEndDateBefore(LocalDateTime date);

    List<Tournament> findByStartDateIsNull();

    List<Tournament> findByEndDateIsNull();

    List<Tournament> findByStatusOrderByStartDateAsc(TournamentStatus status);

    List<Tournament> findAllByOrderByStartDateDesc();

    List<Tournament> findAllByOrderByNameAsc();

    @Query("SELECT t FROM Tournament t LEFT JOIN FETCH t.matches LEFT JOIN FETCH t.sport WHERE t.id = :tournamentId")
    Optional<Tournament> findByIdWithMatchesAndSport(@Param("tournamentId") UUID tournamentId);

    @Query("SELECT t FROM Tournament t LEFT JOIN FETCH t.matches WHERE t.id = :tournamentId")
    Optional<Tournament> findByIdWithMatches(@Param("tournamentId") UUID tournamentId);

    @Query("SELECT t FROM Tournament t LEFT JOIN FETCH t.sport WHERE t.id = :tournamentId")
    Optional<Tournament> findByIdWithSport(@Param("tournamentId") UUID tournamentId);

    @Query("SELECT t FROM Tournament t WHERE t.status = 'UPCOMING' AND t.startDate > :now ORDER BY t.startDate ASC")
    List<Tournament> findUpcomingTournaments(@Param("now") LocalDateTime now);

    @Query("SELECT t FROM Tournament t WHERE t.status = 'ACTIVE' AND t.startDate <= :now AND (t.endDate IS NULL OR t.endDate >= :now)")
    List<Tournament> findActiveTournaments(@Param("now") LocalDateTime now);

    @Query("SELECT t FROM Tournament t WHERE t.status = 'FINISHED' OR (t.endDate IS NOT NULL AND t.endDate < :now)")
    List<Tournament> findFinishedTournaments(@Param("now") LocalDateTime now);

    @Query("SELECT t FROM Tournament t WHERE t.status = 'ACTIVE' OR (t.status = 'UPCOMING' AND t.startDate <= :now)")
    List<Tournament> findStartedTournaments(@Param("now") LocalDateTime now);

    @Query("SELECT t FROM Tournament t WHERE t.status = 'CANCELLED'")
    List<Tournament> findCancelledTournaments();

    @Query("SELECT t FROM Tournament t WHERE t.status != 'FINISHED' AND t.status != 'CANCELLED'")
    List<Tournament> findNotFinishedTournaments();

    @Query("SELECT COUNT(m) FROM Tournament t JOIN t.matches m WHERE t.id = :tournamentId")
    long countMatchesByTournamentId(@Param("tournamentId") UUID tournamentId);

    @Query("SELECT COUNT(m) FROM Tournament t JOIN t.matches m WHERE t.id = :tournamentId AND m.matchDate > :now")
    long countUpcomingMatchesByTournamentId(@Param("tournamentId") UUID tournamentId, @Param("now") LocalDateTime now);

    @Query("SELECT COUNT(m) FROM Tournament t JOIN t.matches m WHERE t.id = :tournamentId AND m.matchDate < :now")
    long countFinishedMatchesByTournamentId(@Param("tournamentId") UUID tournamentId, @Param("now") LocalDateTime now);

    @Query("SELECT t.id, COUNT(m) FROM Tournament t LEFT JOIN t.matches m GROUP BY t.id ORDER BY COUNT(m) DESC")
    List<Object[]> countMatchesByTournament();

    @Query("SELECT t.sport.id, COUNT(t) FROM Tournament t GROUP BY t.sport.id")
    List<Object[]> countTournamentsBySport();

    @Query("SELECT t.status, COUNT(t) FROM Tournament t GROUP BY t.status")
    List<Object[]> countTournamentsByStatus();

    @Query("SELECT t FROM Tournament t WHERE t.description IS NOT NULL AND t.description != ''")
    List<Tournament> findWithDescription();

    @Query("SELECT t FROM Tournament t WHERE t.description IS NULL OR t.description = ''")
    List<Tournament> findWithoutDescription();

    @Query("SELECT t FROM Tournament t WHERE t.startDate IS NOT NULL AND t.endDate IS NOT NULL")
    List<Tournament> findWithStartAndEndDate();

    @Query("SELECT t FROM Tournament t WHERE t.startDate IS NULL OR t.endDate IS NULL")
    List<Tournament> findWithoutStartOrEndDate();

    @Query("SELECT t FROM Tournament t WHERE t.startDate >= :now AND t.status = 'UPCOMING'")
    List<Tournament> findUpcomingTournamentsStartingAfter(@Param("now") LocalDateTime now);

    @Query("SELECT t FROM Tournament t WHERE t.endDate <= :now AND t.status != 'FINISHED' AND t.status != 'CANCELLED'")
    List<Tournament> findTournamentsThatShouldBeFinished(@Param("now") LocalDateTime now);

    @Query("SELECT t FROM Tournament t WHERE t.sport.id = :sportId AND t.status = 'ACTIVE'")
    List<Tournament> findActiveTournamentsBySportId(@Param("sportId") UUID sportId);

    @Query("SELECT t FROM Tournament t WHERE t.sport.id = :sportId AND t.status = 'UPCOMING' ORDER BY t.startDate ASC")
    List<Tournament> findUpcomingTournamentsBySportId(@Param("sportId") UUID sportId);

    @Query("SELECT t FROM Tournament t WHERE t.sport.id = :sportId AND t.status = 'FINISHED' ORDER BY t.endDate DESC")
    List<Tournament> findFinishedTournamentsBySportId(@Param("sportId") UUID sportId);

    @Query("SELECT t FROM Tournament t WHERE t.id IN (SELECT DISTINCT m.tournament.id FROM Match m WHERE m.matchDate BETWEEN :start AND :end)")
    List<Tournament> findTournamentsWithMatchesInDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT t FROM Tournament t WHERE t.id NOT IN (SELECT DISTINCT m.tournament.id FROM Match m)")
    List<Tournament> findTournamentsWithoutMatches();

    @Query("SELECT t FROM Tournament t WHERE t.status = 'UPCOMING' AND t.startDate IS NOT NULL AND t.startDate < :now")
    List<Tournament> findDelayedTournaments(@Param("now") LocalDateTime now);

    @Query("SELECT t FROM Tournament t WHERE FUNCTION('YEAR', t.startDate) = :year")
    List<Tournament> findTournamentsByYear(@Param("year") int year);

    boolean existsByNameIgnoreCase(String name);

    @Query("SELECT COUNT(t) > 0 FROM Tournament t WHERE t.name = :name AND t.sport.id = :sportId")
    boolean existsByNameAndSportId(@Param("name") String name, @Param("sportId") UUID sportId);

    @Query("SELECT t FROM Tournament t WHERE t.startDate IS NOT NULL ORDER BY t.startDate DESC")
    List<Tournament> findLatestTournaments();
}