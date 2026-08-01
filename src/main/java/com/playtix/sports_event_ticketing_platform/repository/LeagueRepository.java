package com.playtix.sports_event_ticketing_platform.repository;

import com.playtix.sports_event_ticketing_platform.domain.entity.League;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LeagueRepository extends JpaRepository<League, UUID> {

    Optional<League> findByName(String name);

    List<League> findByNameContainingIgnoreCase(String name);

    List<League> findByCountry(String country);

    List<League> findByCountryContainingIgnoreCase(String country);

    List<League> findBySeason(String season);

    List<League> findBySeasonContainingIgnoreCase(String season);


    List<League> findBySport_Id(UUID sportId);

    List<League> findBySport_IdOrderByNameAsc(UUID sportId);

    List<League> findByNumberOfTeams(Integer numberOfTeams);

    List<League> findByNumberOfTeamsBetween(Integer minTeams, Integer maxTeams);

    List<League> findByNumberOfTeamsGreaterThan(Integer numberOfTeams);

    List<League> findByNumberOfTeamsLessThan(Integer numberOfTeams);

    List<League> findAllByOrderByNameAsc();

    List<League> findAllByOrderByCountryAsc();

    @Query("SELECT l FROM League l LEFT JOIN FETCH l.matches WHERE l.id = :leagueId")
    Optional<League> findByIdWithMatches(@Param("leagueId") UUID leagueId);

    @Query("SELECT l FROM League l LEFT JOIN FETCH l.sport WHERE l.id = :leagueId")
    Optional<League> findByIdWithSport(@Param("leagueId") UUID leagueId);

    @Query("SELECT l FROM League l LEFT JOIN FETCH l.matches LEFT JOIN FETCH l.sport WHERE l.id = :leagueId")
    Optional<League> findByIdWithAllRelationships(@Param("leagueId") UUID leagueId);

    @Query("SELECT l FROM League l LEFT JOIN FETCH l.matches m WHERE m.matchDate > :now")
    List<League> findLeaguesWithUpcomingMatches(@Param("now") java.time.LocalDateTime now);

    @Query("SELECT l FROM League l LEFT JOIN FETCH l.matches m WHERE m.matchDate < :now")
    List<League> findLeaguesWithFinishedMatches(@Param("now") java.time.LocalDateTime now);

    @Query("SELECT l FROM League l WHERE l.id IN (SELECT DISTINCT m.league.id FROM Match m WHERE m.matchDate > :now)")
    List<League> findLeaguesWithFutureMatches(@Param("now") java.time.LocalDateTime now);

    @Query("SELECT l FROM League l WHERE l.id IN (SELECT DISTINCT m.league.id FROM Match m WHERE m.matchDate < :now)")
    List<League> findLeaguesWithPastMatches(@Param("now") java.time.LocalDateTime now);

    @Query("SELECT l FROM League l WHERE l.id IN (SELECT DISTINCT m.league.id FROM Match m)")
    List<League> findLeaguesWithAnyMatch();

    @Query("SELECT l FROM League l WHERE l.id NOT IN (SELECT DISTINCT m.league.id FROM Match m)")
    List<League> findLeaguesWithoutMatches();

    @Query("SELECT COUNT(m) FROM League l JOIN l.matches m WHERE l.id = :leagueId AND m.matchDate > :now")
    long countUpcomingMatchesByLeagueId(@Param("leagueId") UUID leagueId, @Param("now") java.time.LocalDateTime now);

    @Query("SELECT COUNT(m) FROM League l JOIN l.matches m WHERE l.id = :leagueId AND m.matchDate < :now")
    long countFinishedMatchesByLeagueId(@Param("leagueId") UUID leagueId, @Param("now") java.time.LocalDateTime now);

    @Query("SELECT COUNT(m) FROM League l JOIN l.matches m WHERE l.id = :leagueId")
    long countAllMatchesByLeagueId(@Param("leagueId") UUID leagueId);

    @Query("SELECT l.id, COUNT(m) FROM League l LEFT JOIN l.matches m GROUP BY l.id ORDER BY COUNT(m) DESC")
    List<Object[]> countMatchesByLeague();

    @Query("SELECT l.country, COUNT(l) FROM League l GROUP BY l.country")
    List<Object[]> countLeaguesByCountry();

    @Query("SELECT l.sport.id, COUNT(l) FROM League l GROUP BY l.sport.id")
    List<Object[]> countLeaguesBySport();

    @Query("SELECT l FROM League l WHERE l.description IS NOT NULL AND l.description != ''")
    List<League> findWithDescription();

    @Query("SELECT l FROM League l WHERE l.description IS NULL OR l.description = ''")
    List<League> findWithoutDescription();

    @Query("SELECT l FROM League l WHERE l.numberOfTeams = (SELECT MAX(l2.numberOfTeams) FROM League l2)")
    List<League> findLeaguesWithMostTeams();

    @Query("SELECT l FROM League l WHERE l.numberOfTeams = (SELECT MIN(l2.numberOfTeams) FROM League l2)")
    List<League> findLeaguesWithFewestTeams();

    @Query("SELECT AVG(l.numberOfTeams) FROM League l WHERE l.numberOfTeams IS NOT NULL")
    Double averageNumberOfTeams();

    @Query("SELECT l FROM League l WHERE l.sport.name = :sportName")
    List<League> findBySportName(@Param("sportName") String sportName);

    @Query("SELECT l FROM League l WHERE l.sport.id = :sportId AND l.country = :country")
    List<League> findBySportIdAndCountry(@Param("sportId") UUID sportId, @Param("country") String country);

    @Query("SELECT l FROM League l WHERE l.sport.id = :sportId AND l.season = :season")
    List<League> findBySportIdAndSeason(@Param("sportId") UUID sportId, @Param("season") String season);

    @Query("SELECT l FROM League l WHERE l.country = :country AND l.season = :season")
    List<League> findByCountryAndSeason(@Param("country") String country, @Param("season") String season);

    @Query("SELECT l FROM League l WHERE l.name LIKE %:keyword% OR l.country LIKE %:keyword%")
    List<League> searchByNameOrCountry(@Param("keyword") String keyword);

    boolean existsByNameIgnoreCase(String name);

    @Query("SELECT COUNT(l) > 0 FROM League l WHERE l.name = :name AND l.country = :country")
    boolean existsByNameAndCountry(@Param("name") String name, @Param("country") String country);

    @Query("SELECT l FROM League l WHERE l.sport.id = :sportId AND l.numberOfTeams IS NOT NULL ORDER BY l.numberOfTeams DESC")
    List<League> findBySportIdOrderByNumberOfTeamsDesc(@Param("sportId") UUID sportId);
}