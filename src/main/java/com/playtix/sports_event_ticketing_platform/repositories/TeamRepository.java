package com.playtix.sports_event_ticketing_platform.repositories;

import com.playtix.sports_event_ticketing_platform.domain.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TeamRepository extends JpaRepository<Team, UUID> {

    Optional<Team> findByName(String name);

    List<Team> findByNameContainingIgnoreCase(String name);

    List<Team> findByCity(String city);

    List<Team> findByCityContainingIgnoreCase(String city);

    List<Team> findByHomeStadium(String homeStadium);

    List<Team> findByHomeStadiumContainingIgnoreCase(String homeStadium);

    List<Team> findByFoundedYear(Integer year);

    List<Team> findByFoundedYearBetween(Integer startYear, Integer endYear);

    List<Team> findByFoundedYearLessThan(Integer year);

    List<Team> findByFoundedYearGreaterThan(Integer year);

    List<Team> findByCoach(String coach);

    List<Team> findByCoachContainingIgnoreCase(String coach);

    List<Team> findByCityOrderByNameAsc(String city);

    List<Team> findAllByOrderByNameAsc();

    List<Team> findAllByOrderByFoundedYearDesc();

    @Query("SELECT t FROM Team t LEFT JOIN FETCH t.homeMatches LEFT JOIN FETCH t.awayMatches WHERE t.id = :teamId")
    Optional<Team> findByIdWithMatches(@Param("teamId") UUID teamId);

    @Query("SELECT t FROM Team t LEFT JOIN FETCH t.homeMatches h WHERE h.matchDate > :now")
    List<Team> findTeamsWithUpcomingHomeMatches(@Param("now") java.time.LocalDateTime now);

    @Query("SELECT t FROM Team t LEFT JOIN FETCH t.awayMatches a WHERE a.matchDate > :now")
    List<Team> findTeamsWithUpcomingAwayMatches(@Param("now") java.time.LocalDateTime now);

    @Query("SELECT t FROM Team t WHERE t.id IN (SELECT DISTINCT m.homeTeam.id FROM Match m WHERE m.matchDate > :now)")
    List<Team> findTeamsWithHomeMatchesInFuture(@Param("now") java.time.LocalDateTime now);

    @Query("SELECT t FROM Team t WHERE t.id IN (SELECT DISTINCT m.awayTeam.id FROM Match m WHERE m.matchDate > :now)")
    List<Team> findTeamsWithAwayMatchesInFuture(@Param("now") java.time.LocalDateTime now);

    @Query("SELECT t FROM Team t WHERE t.id IN (SELECT DISTINCT m.homeTeam.id FROM Match m UNION SELECT DISTINCT m.awayTeam.id FROM Match m)")
    List<Team> findTeamsWithAnyMatch();

    @Query("SELECT t FROM Team t WHERE t.id NOT IN (SELECT DISTINCT m.homeTeam.id FROM Match m UNION SELECT DISTINCT m.awayTeam.id FROM Match m)")
    List<Team> findTeamsWithoutMatches();

    @Query("SELECT COUNT(h) FROM Team t JOIN t.homeMatches h WHERE t.id = :teamId AND h.matchDate > :now")
    long countUpcomingHomeMatchesByTeamId(@Param("teamId") UUID teamId, @Param("now") java.time.LocalDateTime now);

    @Query("SELECT COUNT(a) FROM Team t JOIN t.awayMatches a WHERE t.id = :teamId AND a.matchDate > :now")
    long countUpcomingAwayMatchesByTeamId(@Param("teamId") UUID teamId, @Param("now") java.time.LocalDateTime now);

    @Query("SELECT COUNT(h) FROM Team t JOIN t.homeMatches h WHERE t.id = :teamId")
    long countAllHomeMatchesByTeamId(@Param("teamId") UUID teamId);

    @Query("SELECT COUNT(a) FROM Team t JOIN t.awayMatches a WHERE t.id = :teamId")
    long countAllAwayMatchesByTeamId(@Param("teamId") UUID teamId);

    @Query("SELECT t.id, COUNT(h) + COUNT(a) FROM Team t LEFT JOIN t.homeMatches h LEFT JOIN t.awayMatches a GROUP BY t.id")
    List<Object[]> countAllMatchesByTeam();

    @Query("SELECT t.id, COUNT(h) FROM Team t LEFT JOIN t.homeMatches h GROUP BY t.id ORDER BY COUNT(h) DESC")
    List<Object[]> countHomeMatchesByTeam();

    @Query("SELECT t.id, COUNT(a) FROM Team t LEFT JOIN t.awayMatches a GROUP BY t.id ORDER BY COUNT(a) DESC")
    List<Object[]> countAwayMatchesByTeam();

    @Query("SELECT t FROM Team t WHERE t.foundedYear IS NOT NULL")
    List<Team> findWithFoundedYear();

    @Query("SELECT t FROM Team t WHERE t.foundedYear IS NULL")
    List<Team> findWithoutFoundedYear();

    @Query("SELECT t FROM Team t WHERE t.logo IS NOT NULL AND t.logo != ''")
    List<Team> findWithLogo();

    @Query("SELECT t FROM Team t WHERE t.logo IS NULL OR t.logo = ''")
    List<Team> findWithoutLogo();

    @Query("SELECT t FROM Team t WHERE t.description IS NOT NULL AND t.description != ''")
    List<Team> findWithDescription();

    @Query("SELECT t FROM Team t WHERE t.coach IS NOT NULL AND t.coach != ''")
    List<Team> findWithCoach();

    @Query("SELECT t FROM Team t WHERE t.id IN (SELECT DISTINCT h.homeTeam.id FROM Match h JOIN h.ticketCategories tc WHERE tc.remainingCapacity > 0)")
    List<Team> findTeamsWithAvailableTickets();

    @Query("SELECT t FROM Team t WHERE t.id IN (SELECT DISTINCT h.homeTeam.id FROM Match h WHERE h.matchDate > :now) OR t.id IN (SELECT DISTINCT a.awayTeam.id FROM Match a WHERE a.matchDate > :now)")
    List<Team> findTeamsWithUpcomingMatches(@Param("now") java.time.LocalDateTime now);

    @Query("SELECT t FROM Team t WHERE t.id IN (SELECT DISTINCT h.homeTeam.id FROM Match h WHERE h.matchDate < :now) OR t.id IN (SELECT DISTINCT a.awayTeam.id FROM Match a WHERE a.matchDate < :now)")
    List<Team> findTeamsWithFinishedMatches(@Param("now") java.time.LocalDateTime now);

    @Query("SELECT t.city, COUNT(t) FROM Team t GROUP BY t.city")
    List<Object[]> countTeamsByCity();

    @Query("SELECT AVG(t.foundedYear) FROM Team t WHERE t.foundedYear IS NOT NULL")
    Double averageFoundedYear();

    @Query("SELECT MAX(t.foundedYear) FROM Team t")
    Integer findLatestFoundedYear();

    @Query("SELECT MIN(t.foundedYear) FROM Team t")
    Integer findEarliestFoundedYear();

    boolean existsByNameIgnoreCase(String name);

    @Query("SELECT COUNT(t) > 0 FROM Team t WHERE t.name = :name AND t.city = :city")
    boolean existsByNameAndCity(@Param("name") String name, @Param("city") String city);
}