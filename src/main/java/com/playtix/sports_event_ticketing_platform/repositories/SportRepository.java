package com.playtix.sports_event_ticketing_platform.repositories;

import com.playtix.sports_event_ticketing_platform.domain.entity.Sport;
import com.playtix.sports_event_ticketing_platform.domain.entity.SportType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SportRepository extends JpaRepository<Sport, UUID> {

    Optional<Sport> findByName(SportType name);

    List<Sport> findByDescriptionContainingIgnoreCase(String description);

    List<Sport> findByNumberOfPlayers(int numberOfPlayers);

    List<Sport> findByNumberOfPlayersBetween(int minPlayers, int maxPlayers);

    List<Sport> findByNumberOfPlayersGreaterThan(int numberOfPlayers);

    List<Sport> findByNumberOfPlayersLessThan(int numberOfPlayers);

    List<Sport> findAllByOrderByNameAsc();

    @Query("SELECT s FROM Sport s LEFT JOIN FETCH s.leagues WHERE s.id = :sportId")
    Optional<Sport> findByIdWithLeagues(@Param("sportId") UUID sportId);

    @Query("SELECT s FROM Sport s LEFT JOIN FETCH s.tournaments WHERE s.id = :sportId")
    Optional<Sport> findByIdWithTournaments(@Param("sportId") UUID sportId);

    @Query("SELECT s FROM Sport s LEFT JOIN FETCH s.leagues LEFT JOIN FETCH s.tournaments WHERE s.id = :sportId")
    Optional<Sport> findByIdWithAllRelationships(@Param("sportId") UUID sportId);

    @Query("SELECT s FROM Sport s LEFT JOIN FETCH s.leagues l WHERE l.id IS NOT NULL")
    List<Sport> findSportsWithLeagues();

    @Query("SELECT s FROM Sport s LEFT JOIN FETCH s.tournaments t WHERE t.id IS NOT NULL")
    List<Sport> findSportsWithTournaments();

    @Query("SELECT s FROM Sport s WHERE s.id IN (SELECT DISTINCT l.sport.id FROM League l)")
    List<Sport> findSportsWithAtLeastOneLeague();

    @Query("SELECT s FROM Sport s WHERE s.id IN (SELECT DISTINCT t.sport.id FROM Tournament t)")
    List<Sport> findSportsWithAtLeastOneTournament();

    @Query("SELECT s FROM Sport s WHERE s.id NOT IN (SELECT DISTINCT l.sport.id FROM League l)")
    List<Sport> findSportsWithoutLeagues();

    @Query("SELECT s FROM Sport s WHERE s.id NOT IN (SELECT DISTINCT t.sport.id FROM Tournament t)")
    List<Sport> findSportsWithoutTournaments();

    @Query("SELECT s FROM Sport s WHERE s.id NOT IN (SELECT DISTINCT l.sport.id FROM League l) AND s.id NOT IN (SELECT DISTINCT t.sport.id FROM Tournament t)")
    List<Sport> findSportsWithoutAnyAssociation();

    @Query("SELECT COUNT(l) FROM Sport s JOIN s.leagues l WHERE s.id = :sportId")
    long countLeaguesBySportId(@Param("sportId") UUID sportId);

    @Query("SELECT COUNT(t) FROM Sport s JOIN s.tournaments t WHERE s.id = :sportId")
    long countTournamentsBySportId(@Param("sportId") UUID sportId);

    @Query("SELECT s.id, COUNT(l) FROM Sport s LEFT JOIN s.leagues l GROUP BY s.id ORDER BY COUNT(l) DESC")
    List<Object[]> countLeaguesBySport();

    @Query("SELECT s.id, COUNT(t) FROM Sport s LEFT JOIN s.tournaments t GROUP BY s.id ORDER BY COUNT(t) DESC")
    List<Object[]> countTournamentsBySport();

    @Query("SELECT s.id, COUNT(l) + COUNT(t) FROM Sport s LEFT JOIN s.leagues l LEFT JOIN s.tournaments t GROUP BY s.id ORDER BY (COUNT(l) + COUNT(t)) DESC")
    List<Object[]> countTotalAssociationsBySport();

    @Query("SELECT s FROM Sport s WHERE s.description IS NOT NULL AND s.description != ''")
    List<Sport> findWithDescription();

    @Query("SELECT s FROM Sport s WHERE s.description IS NULL OR s.description = ''")
    List<Sport> findWithoutDescription();

    @Query("SELECT s FROM Sport s WHERE s.numberOfPlayers = (SELECT MAX(s2.numberOfPlayers) FROM Sport s2)")
    List<Sport> findSportsWithMostPlayers();

    @Query("SELECT s FROM Sport s WHERE s.numberOfPlayers = (SELECT MIN(s2.numberOfPlayers) FROM Sport s2)")
    List<Sport> findSportsWithFewestPlayers();

    @Query("SELECT AVG(s.numberOfPlayers) FROM Sport s")
    Double averageNumberOfPlayers();

    @Query("SELECT s FROM Sport s WHERE s.numberOfPlayers > (SELECT AVG(s2.numberOfPlayers) FROM Sport s2)")
    List<Sport> findSportsAboveAveragePlayers();

    @Query("SELECT s FROM Sport s WHERE s.numberOfPlayers < (SELECT AVG(s2.numberOfPlayers) FROM Sport s2)")
    List<Sport> findSportsBelowAveragePlayers();

    @Query("SELECT s.name, COUNT(l) FROM Sport s LEFT JOIN s.leagues l GROUP BY s.name")
    List<Object[]> countLeaguesBySportName();

    @Query("SELECT s.name, COUNT(t) FROM Sport s LEFT JOIN s.tournaments t GROUP BY s.name")
    List<Object[]> countTournamentsBySportName();

    @Query("SELECT s FROM Sport s WHERE s.id IN (SELECT DISTINCT m.sport.id FROM League m) AND s.id IN (SELECT DISTINCT t.sport.id FROM Tournament t)")
    List<Sport> findSportsWithBothLeaguesAndTournaments();

    @Query("SELECT s FROM Sport s WHERE s.id IN (SELECT DISTINCT m.sport.id FROM League m) OR s.id IN (SELECT DISTINCT t.sport.id FROM Tournament t)")
    List<Sport> findSportsWithLeaguesOrTournaments();

    boolean existsByName(SportType name);

    @Query("SELECT COUNT(s) > 0 FROM Sport s WHERE s.name = :name")
    boolean existsBySportType(@Param("name") SportType name);

    @Query("SELECT s FROM Sport s ORDER BY s.numberOfPlayers DESC")
    List<Sport> findAllOrderByNumberOfPlayersDesc();

    @Query("SELECT s FROM Sport s WHERE LOWER(s.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Sport> searchByDescriptionKeyword(@Param("keyword") String keyword);
}