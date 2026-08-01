package com.playtix.sports_event_ticketing_platform.repositories;

import com.playtix.sports_event_ticketing_platform.domain.entity.members.AccountStatus;
import com.playtix.sports_event_ticketing_platform.domain.entity.members.Support;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SupportRepository extends JpaRepository<Support, UUID> {

    Optional<Support> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Support> findByFirstname(String firstname);

    List<Support> findByLastname(String lastname);

    List<Support> findByCity(String city);

    List<Support> findByStatus(AccountStatus status);

    List<Support> findByFirstnameAndLastname(String firstname, String lastname);

    Optional<Support> findByPhoneNumber(String phoneNumber);

    List<Support> findByRegistrationDateAfter(LocalDateTime date);

    List<Support> findByRegistrationDateBefore(LocalDateTime date);

    @Query("SELECT s FROM Support s LEFT JOIN FETCH s.reports WHERE s.id = :supportId")
    Optional<Support> findByIdWithReports(@Param("supportId") UUID supportId);

    @Query("SELECT s FROM Support s WHERE s.status = 'ACTIVE'")
    List<Support> findActiveSupports();

    @Query("SELECT s FROM Support s WHERE " +
           "(SELECT COUNT(r) FROM Report r WHERE r.support = s AND r.status = 'PENDING') > :threshold")
    List<Support> findSupportsWithPendingReportsGreaterThan(@Param("threshold") long threshold);

    @Query("SELECT s FROM Support s WHERE " +
           "(SELECT COUNT(r) FROM Report r WHERE r.support = s AND r.status = 'PENDING') = 0")
    List<Support> findSupportsWithNoPendingReports();

    @Query("SELECT s FROM Support s ORDER BY SIZE(s.reports) DESC")
    List<Support> findAllOrderByReportsCountDesc();

    @Query("SELECT s, COUNT(r) FROM Support s LEFT JOIN s.reports r GROUP BY s")
    List<Object[]> findSupportsWithReportCount();

    @Query("SELECT s FROM Support s WHERE s.status = 'ACTIVE' AND " +
           "(SELECT COUNT(r) FROM Report r WHERE r.support = s AND r.status = 'PENDING') <= 5")
    List<Support> findAvailableSupports();

    List<Support> findByStatusOrderByRegistrationDateDesc(AccountStatus status);

    long countByStatus(AccountStatus status);

    long countPendingReportsBySupportId(@Param("supportId") UUID supportId);

    @Query("SELECT DISTINCT s FROM Support s JOIN s.reports r WHERE r.status = 'RESOLVED'")
    List<Support> findSupportsWithResolvedReports();

    @Query("SELECT s FROM Support s WHERE SIZE(s.reports) = (SELECT MAX(SIZE(s2.reports)) FROM Support s2)")
    List<Support> findSupportsWithMaxReports();
    
}