package com.playtix.sports_event_ticketing_platform.repositories;

import com.playtix.sports_event_ticketing_platform.domain.entity.report.Report;
import com.playtix.sports_event_ticketing_platform.domain.entity.report.ReportStatus;
import com.playtix.sports_event_ticketing_platform.domain.entity.report.ReportSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReportRepository extends JpaRepository<Report, UUID> {

    List<Report> findByStatus(ReportStatus status);

    List<Report> findBySubject(ReportSubject subject);

    List<Report> findByUser_Id(UUID userId);

    List<Report> findBySupport_Id(UUID supportId);

    List<Report> findByTicket_Id(UUID ticketId);

    List<Report> findByStatusAndSupport_Id(ReportStatus status, UUID supportId);

    List<Report> findByStatusAndUser_Id(ReportStatus status, UUID userId);

    List<Report> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    List<Report> findByStatusOrderByCreatedAtAsc(ReportStatus status);

    List<Report> findBySupportIsNull();

    List<Report> findBySupportIsNotNull();

    long countByStatus(ReportStatus status);

    long countByUser_Id(UUID userId);

    long countBySupport_Id(UUID supportId);

    long countByStatusAndSupport_Id(ReportStatus status, UUID supportId);

    @Query("SELECT r FROM Report r LEFT JOIN FETCH r.user LEFT JOIN FETCH r.support LEFT JOIN FETCH r.ticket WHERE r.id = :reportId")
    Optional<Report> findByIdWithAllRelationships(@Param("reportId") UUID reportId);

    @Query("SELECT r FROM Report r LEFT JOIN FETCH r.user LEFT JOIN FETCH r.ticket WHERE r.id = :reportId")
    Optional<Report> findByIdWithUserAndTicket(@Param("reportId") UUID reportId);

    @Query("SELECT r FROM Report r LEFT JOIN FETCH r.support WHERE r.id = :reportId")
    Optional<Report> findByIdWithSupport(@Param("reportId") UUID reportId);

    @Query("SELECT r FROM Report r WHERE r.status = 'PENDING' ORDER BY r.createdAt ASC")
    List<Report> findPendingReportsOrderByCreatedAt();

    @Query("SELECT r FROM Report r WHERE r.status = 'PENDING' AND r.support IS NULL")
    List<Report> findUnassignedPendingReports();

    @Query("SELECT r FROM Report r WHERE r.support.id = :supportId AND r.status IN ('PENDING', 'IN_PROGRESS')")
    List<Report> findActiveReportsBySupportId(@Param("supportId") UUID supportId);

    @Query("SELECT r FROM Report r WHERE r.user.id = :userId AND r.status != 'RESOLVED' AND r.status != 'REJECTED'")
    List<Report> findOpenReportsByUserId(@Param("userId") UUID userId);

    @Query("SELECT r FROM Report r WHERE r.user.id = :userId AND r.status = 'RESOLVED'")
    List<Report> findResolvedReportsByUserId(@Param("userId") UUID userId);

    @Query("SELECT COUNT(r) FROM Report r WHERE r.support.id = :supportId AND r.status = 'PENDING'")
    long countPendingReportsBySupportId(@Param("supportId") UUID supportId);

    @Query("SELECT r.subject, COUNT(r) FROM Report r GROUP BY r.subject")
    List<Object[]> countReportsBySubject();

    @Query("SELECT r.status, COUNT(r) FROM Report r GROUP BY r.status")
    List<Object[]> countReportsByStatus();

    @Query("SELECT r.user.id, COUNT(r) FROM Report r GROUP BY r.user.id ORDER BY COUNT(r) DESC")
    List<Object[]> findUsersWithMostReports();

    @Query("SELECT r.support.id, COUNT(r) FROM Report r WHERE r.support IS NOT NULL GROUP BY r.support.id ORDER BY COUNT(r) DESC")
    List<Object[]> findSupportsWithMostReports();

    @Query("SELECT r FROM Report r WHERE r.updatedAt IS NOT NULL AND r.updatedAt >= :since")
    List<Report> findReportsUpdatedSince(@Param("since") LocalDateTime since);

    @Query("SELECT r FROM Report r WHERE r.createdAt >= :start AND r.createdAt <= :end AND r.status = :status")
    List<Report> findReportsByDateRangeAndStatus(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("status") ReportStatus status);

    @Query("SELECT r FROM Report r WHERE r.user.id = :userId AND r.ticket.id = :ticketId")
    List<Report> findByUserIdAndTicketId(@Param("userId") UUID userId, @Param("ticketId") UUID ticketId);

    boolean existsByTicket_IdAndStatusIn(UUID ticketId, List<ReportStatus> statuses);

    @Query("SELECT r FROM Report r WHERE r.support.id = :supportId AND r.status = 'PENDING'")
    List<Report> findPendingReportsBySupportId(@Param("supportId") UUID supportId);

    @Query("SELECT r FROM Report r WHERE r.support.id = :supportId AND r.status = 'IN_PROGRESS'")
    List<Report> findInProgressReportsBySupportId(@Param("supportId") UUID supportId);

    @Query("SELECT r FROM Report r WHERE r.support.id = :supportId AND r.status = 'RESOLVED'")
    List<Report> findResolvedReportsBySupportId(@Param("supportId") UUID supportId);

    @Query("SELECT r FROM Report r WHERE r.support.id = :supportId AND r.status = 'REJECTED'")
    List<Report> findRejectedReportsBySupportId(@Param("supportId") UUID supportId);

    @Query("SELECT r FROM Report r WHERE r.support.id = :supportId AND r.status = 'REOPENED'")
    List<Report> findReopenedReportsBySupportId(@Param("supportId") UUID supportId);
}