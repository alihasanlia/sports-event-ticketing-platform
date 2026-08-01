package com.playtix.sports_event_ticketing_platform.repository;

import com.playtix.sports_event_ticketing_platform.domain.entity.Stadium;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StadiumRepository extends JpaRepository<Stadium, UUID> {

    List<Stadium> findByNameContainingIgnoreCase(String name);

    List<Stadium> findByCity(String city);

    List<Stadium> findByCityContainingIgnoreCase(String city);

    List<Stadium> findByCapacityGreaterThanEqual(int capacity);

    List<Stadium> findByCapacityLessThanEqual(int capacity);

    List<Stadium> findByCapacityBetween(int minCapacity, int maxCapacity);

    List<Stadium> findByAddressContainingIgnoreCase(String address);

    Optional<Stadium> findByName(String name);

    List<Stadium> findByCityOrderByNameAsc(String city);

    List<Stadium> findAllByOrderByNameAsc();

    List<Stadium> findAllByOrderByCapacityDesc();

    @Query("SELECT s FROM Stadium s LEFT JOIN FETCH s.matches WHERE s.id = :stadiumId")
    Optional<Stadium> findByIdWithMatches(@Param("stadiumId") UUID stadiumId);

    @Query("SELECT s FROM Stadium s LEFT JOIN FETCH s.matches m WHERE m.matchDate > :now")
    List<Stadium> findStadiumsWithUpcomingMatches(@Param("now") java.time.LocalDateTime now);

    @Query("SELECT s FROM Stadium s LEFT JOIN FETCH s.matches m WHERE m.matchDate < :now")
    List<Stadium> findStadiumsWithFinishedMatches(@Param("now") java.time.LocalDateTime now);

    @Query("SELECT s FROM Stadium s WHERE s.id IN (SELECT DISTINCT m.stadium.id FROM Match m WHERE m.matchDate > :now)")
    List<Stadium> findStadiumsWithFutureMatches(@Param("now") java.time.LocalDateTime now);

    @Query("SELECT s FROM Stadium s WHERE s.id IN (SELECT DISTINCT m.stadium.id FROM Match m WHERE m.matchDate < :now)")
    List<Stadium> findStadiumsWithPastMatches(@Param("now") java.time.LocalDateTime now);

    @Query("SELECT s FROM Stadium s WHERE s.id IN (SELECT DISTINCT m.stadium.id FROM Match m WHERE m.matchDate BETWEEN :start AND :end)")
    List<Stadium> findStadiumsWithMatchesInDateRange(@Param("start") java.time.LocalDateTime start, @Param("end") java.time.LocalDateTime end);

    @Query("SELECT s FROM Stadium s WHERE s.capacity > (SELECT AVG(s2.capacity) FROM Stadium s2)")
    List<Stadium> findStadiumsAboveAverageCapacity();

    @Query("SELECT s FROM Stadium s WHERE s.capacity < (SELECT AVG(s2.capacity) FROM Stadium s2)")
    List<Stadium> findStadiumsBelowAverageCapacity();

    @Query("SELECT COUNT(m) FROM Stadium s JOIN s.matches m WHERE s.id = :stadiumId AND m.matchDate > :now")
    long countUpcomingMatchesByStadiumId(@Param("stadiumId") UUID stadiumId, @Param("now") java.time.LocalDateTime now);

    @Query("SELECT COUNT(m) FROM Stadium s JOIN s.matches m WHERE s.id = :stadiumId AND m.matchDate < :now")
    long countFinishedMatchesByStadiumId(@Param("stadiumId") UUID stadiumId, @Param("now") java.time.LocalDateTime now);

    @Query("SELECT s.id, COUNT(m) FROM Stadium s LEFT JOIN s.matches m GROUP BY s.id")
    List<Object[]> countMatchesByStadium();

    @Query("SELECT s.id, SUM(m.ticketCategories.SIZE()) FROM Stadium s LEFT JOIN s.matches m LEFT JOIN m.ticketCategories tc GROUP BY s.id")
    List<Object[]> countTicketCategoriesByStadium();

    @Query("SELECT s.city, COUNT(s) FROM Stadium s GROUP BY s.city")
    List<Object[]> countStadiumsByCity();

    @Query("SELECT s.city, AVG(s.capacity) FROM Stadium s GROUP BY s.city")
    List<Object[]> averageCapacityByCity();

    @Query("SELECT s FROM Stadium s WHERE s.capacity = (SELECT MAX(s2.capacity) FROM Stadium s2)")
    List<Stadium> findLargestStadiums();

    @Query("SELECT s FROM Stadium s WHERE s.capacity = (SELECT MIN(s2.capacity) FROM Stadium s2)")
    List<Stadium> findSmallestStadiums();

    @Query("SELECT s FROM Stadium s WHERE s.city = :city AND s.capacity >= :minCapacity")
    List<Stadium> findByCityAndCapacityGreaterThanEqual(@Param("city") String city, @Param("minCapacity") int minCapacity);

    @Query("SELECT s FROM Stadium s WHERE s.city = :city AND s.capacity <= :maxCapacity")
    List<Stadium> findByCityAndCapacityLessThanEqual(@Param("city") String city, @Param("maxCapacity") int maxCapacity);

    @Query("SELECT s FROM Stadium s WHERE s.address IS NOT NULL AND s.address != ''")
    List<Stadium> findStadiumsWithAddress();

    @Query("SELECT s FROM Stadium s WHERE s.address IS NULL OR s.address = ''")
    List<Stadium> findStadiumsWithoutAddress();

    @Query("SELECT COUNT(s) FROM Stadium s WHERE s.city = :city")
    long countByCity(@Param("city") String city);

    @Query("SELECT SUM(s.capacity) FROM Stadium s")
    Integer sumTotalCapacity();

    @Query("SELECT SUM(s.capacity) FROM Stadium s WHERE s.city = :city")
    Integer sumCapacityByCity(@Param("city") String city);

    boolean existsByNameIgnoreCase(String name);

    @Query("SELECT COUNT(s) > 0 FROM Stadium s WHERE s.name = :name AND s.city = :city")
    boolean existsByNameAndCity(@Param("name") String name, @Param("city") String city);
}