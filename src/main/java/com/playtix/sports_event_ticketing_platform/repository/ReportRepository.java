package com.playtix.sports_event_ticketing_platform.repository;

import com.playtix.sports_event_ticketing_platform.domain.entity.report.Report;
import com.playtix.sports_event_ticketing_platform.domain.entity.report.ReportStatus;
import com.playtix.sports_event_ticketing_platform.domain.entity.report.ReportSubject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ReportRepository {

    private final JdbcTemplate jdbcTemplate;

    public ReportRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Report> reportRowMapper = (rs, rowNum) -> {
        Report report = new Report();
        report.setId(rs.getObject("id", UUID.class));
        String subjectStr = rs.getString("subject");
        if (subjectStr != null) {
            report.setSubject(ReportSubject.valueOf(subjectStr));
        }
        report.setDescription(rs.getString("description"));
        Timestamp createdAtTs = rs.getTimestamp("created_at");
        if (createdAtTs != null) {
            report.setCreatedAt(createdAtTs.toLocalDateTime());
        }
        Timestamp updatedAtTs = rs.getTimestamp("updated_at");
        if (updatedAtTs != null) {
            report.setUpdatedAt(updatedAtTs.toLocalDateTime());
        }
        report.setAdminResponse(rs.getString("admin_response"));
        String statusStr = rs.getString("status");
        if (statusStr != null) {
            report.setStatus(ReportStatus.valueOf(statusStr));
        }
        return report;
    };

    public Optional<Report> findById(UUID id) {
        String sql = "SELECT * FROM reports WHERE id = ?";
        List<Report> results = jdbcTemplate.query(sql, reportRowMapper, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public List<Report> findAll() {
        String sql = "SELECT * FROM reports";
        return jdbcTemplate.query(sql, reportRowMapper);
    }

    public Report save(Report report) {
        if (report.getId() == null) {
            report.setId(UUID.randomUUID());
            if (report.getCreatedAt() == null) {
                report.setCreatedAt(LocalDateTime.now());
            }
            if (report.getStatus() == null) {
                report.setStatus(ReportStatus.PENDING);
            }
            String sql = "INSERT INTO reports (id, subject, description, created_at, updated_at, admin_response, status, user_id, support_id, ticket_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(sql,
                    report.getId(),
                    report.getSubject() != null ? report.getSubject().name() : null,
                    report.getDescription(),
                    report.getCreatedAt() != null ? Timestamp.valueOf(report.getCreatedAt()) : null,
                    report.getUpdatedAt() != null ? Timestamp.valueOf(report.getUpdatedAt()) : null,
                    report.getAdminResponse(),
                    report.getStatus() != null ? report.getStatus().name() : null,
                    report.getUser() != null ? report.getUser().getId() : null,
                    report.getSupport() != null ? report.getSupport().getId() : null,
                    report.getTicket() != null ? report.getTicket().getId() : null
            );
        } else {
            report.setUpdatedAt(LocalDateTime.now());
            String sql = "UPDATE reports SET subject = ?, description = ?, updated_at = ?, admin_response = ?, status = ?, user_id = ?, support_id = ?, ticket_id = ? WHERE id = ?";
            jdbcTemplate.update(sql,
                    report.getSubject() != null ? report.getSubject().name() : null,
                    report.getDescription(),
                    report.getUpdatedAt() != null ? Timestamp.valueOf(report.getUpdatedAt()) : null,
                    report.getAdminResponse(),
                    report.getStatus() != null ? report.getStatus().name() : null,
                    report.getUser() != null ? report.getUser().getId() : null,
                    report.getSupport() != null ? report.getSupport().getId() : null,
                    report.getTicket() != null ? report.getTicket().getId() : null,
                    report.getId()
            );
        }
        return report;
    }

    public void deleteById(UUID id) {
        String sql = "DELETE FROM reports WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public void delete(Report report) {
        if (report != null && report.getId() != null) {
            deleteById(report.getId());
        }
    }

    public List<Report> findByStatus(ReportStatus status) {
        String sql = "SELECT * FROM reports WHERE status = ?";
        return jdbcTemplate.query(sql, reportRowMapper, status.name());
    }

    public List<Report> findBySubject(ReportSubject subject) {
        String sql = "SELECT * FROM reports WHERE subject = ?";
        return jdbcTemplate.query(sql, reportRowMapper, subject.name());
    }

    public List<Report> findByUser_Id(UUID userId) {
        String sql = "SELECT * FROM reports WHERE user_id = ?";
        return jdbcTemplate.query(sql, reportRowMapper, userId);
    }

    public List<Report> findBySupport_Id(UUID supportId) {
        String sql = "SELECT * FROM reports WHERE support_id = ?";
        return jdbcTemplate.query(sql, reportRowMapper, supportId);
    }

    public List<Report> findByTicket_Id(UUID ticketId) {
        String sql = "SELECT * FROM reports WHERE ticket_id = ?";
        return jdbcTemplate.query(sql, reportRowMapper, ticketId);
    }

    public List<Report> findByStatusAndSupport_Id(ReportStatus status, UUID supportId) {
        String sql = "SELECT * FROM reports WHERE status = ? AND support_id = ?";
        return jdbcTemplate.query(sql, reportRowMapper, status.name(), supportId);
    }

    public List<Report> findByStatusAndUser_Id(ReportStatus status, UUID userId) {
        String sql = "SELECT * FROM reports WHERE status = ? AND user_id = ?";
        return jdbcTemplate.query(sql, reportRowMapper, status.name(), userId);
    }

    public List<Report> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT * FROM reports WHERE created_at BETWEEN ? AND ?";
        return jdbcTemplate.query(sql, reportRowMapper, Timestamp.valueOf(start), Timestamp.valueOf(end));
    }

    public List<Report> findByStatusOrderByCreatedAtAsc(ReportStatus status) {
        String sql = "SELECT * FROM reports WHERE status = ? ORDER BY created_at ASC";
        return jdbcTemplate.query(sql, reportRowMapper, status.name());
    }

    public List<Report> findBySupportIsNull() {
        String sql = "SELECT * FROM reports WHERE support_id IS NULL";
        return jdbcTemplate.query(sql, reportRowMapper);
    }

    public List<Report> findBySupportIsNotNull() {
        String sql = "SELECT * FROM reports WHERE support_id IS NOT NULL";
        return jdbcTemplate.query(sql, reportRowMapper);
    }

    public long countByStatus(ReportStatus status) {
        String sql = "SELECT COUNT(*) FROM reports WHERE status = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, status.name());
        return count != null ? count : 0;
    }

    public long countByUser_Id(UUID userId) {
        String sql = "SELECT COUNT(*) FROM reports WHERE user_id = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, userId);
        return count != null ? count : 0;
    }

    public long countBySupport_Id(UUID supportId) {
        String sql = "SELECT COUNT(*) FROM reports WHERE support_id = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, supportId);
        return count != null ? count : 0;
    }

    public long countByStatusAndSupport_Id(ReportStatus status, UUID supportId) {
        String sql = "SELECT COUNT(*) FROM reports WHERE status = ? AND support_id = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, status.name(), supportId);
        return count != null ? count : 0;
    }

    public Optional<Report> findByIdWithAllRelationships(UUID reportId) {
        String sql = "SELECT * FROM reports WHERE id = ?";
        List<Report> results = jdbcTemplate.query(sql, reportRowMapper, reportId);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public Optional<Report> findByIdWithUserAndTicket(UUID reportId) {
        String sql = "SELECT * FROM reports WHERE id = ?";
        List<Report> results = jdbcTemplate.query(sql, reportRowMapper, reportId);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public Optional<Report> findByIdWithSupport(UUID reportId) {
        String sql = "SELECT * FROM reports WHERE id = ?";
        List<Report> results = jdbcTemplate.query(sql, reportRowMapper, reportId);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public List<Report> findPendingReportsOrderByCreatedAt() {
        String sql = "SELECT * FROM reports WHERE status = 'PENDING' ORDER BY created_at ASC";
        return jdbcTemplate.query(sql, reportRowMapper);
    }

    public List<Report> findUnassignedPendingReports() {
        String sql = "SELECT * FROM reports WHERE status = 'PENDING' AND support_id IS NULL";
        return jdbcTemplate.query(sql, reportRowMapper);
    }

    public List<Report> findActiveReportsBySupportId(UUID supportId) {
        String sql = "SELECT * FROM reports WHERE support_id = ? AND status IN ('PENDING', 'IN_PROGRESS')";
        return jdbcTemplate.query(sql, reportRowMapper, supportId);
    }

    public List<Report> findOpenReportsByUserId(UUID userId) {
        String sql = "SELECT * FROM reports WHERE user_id = ? AND status != 'RESOLVED' AND status != 'REJECTED'";
        return jdbcTemplate.query(sql, reportRowMapper, userId);
    }

    public List<Report> findResolvedReportsByUserId(UUID userId) {
        String sql = "SELECT * FROM reports WHERE user_id = ? AND status = 'RESOLVED'";
        return jdbcTemplate.query(sql, reportRowMapper, userId);
    }

    public long countPendingReportsBySupportId(UUID supportId) {
        String sql = "SELECT COUNT(*) FROM reports WHERE support_id = ? AND status = 'PENDING'";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, supportId);
        return count != null ? count : 0;
    }

    public List<Object[]> countReportsBySubject() {
        String sql = "SELECT subject, COUNT(*) FROM reports GROUP BY subject";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Object[]{rs.getString(1), rs.getLong(2)});
    }

    public List<Object[]> countReportsByStatus() {
        String sql = "SELECT status, COUNT(*) FROM reports GROUP BY status";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Object[]{rs.getString(1), rs.getLong(2)});
    }

    public List<Object[]> findUsersWithMostReports() {
        String sql = "SELECT user_id, COUNT(*) FROM reports GROUP BY user_id ORDER BY COUNT(*) DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Object[]{rs.getObject(1, UUID.class), rs.getLong(2)});
    }

    public List<Object[]> findSupportsWithMostReports() {
        String sql = "SELECT support_id, COUNT(*) FROM reports WHERE support_id IS NOT NULL GROUP BY support_id ORDER BY COUNT(*) DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Object[]{rs.getObject(1, UUID.class), rs.getLong(2)});
    }

    public List<Report> findReportsUpdatedSince(LocalDateTime since) {
        String sql = "SELECT * FROM reports WHERE updated_at IS NOT NULL AND updated_at >= ?";
        return jdbcTemplate.query(sql, reportRowMapper, Timestamp.valueOf(since));
    }

    public List<Report> findReportsByDateRangeAndStatus(LocalDateTime start, LocalDateTime end, ReportStatus status) {
        String sql = "SELECT * FROM reports WHERE created_at >= ? AND created_at <= ? AND status = ?";
        return jdbcTemplate.query(sql, reportRowMapper, Timestamp.valueOf(start), Timestamp.valueOf(end), status.name());
    }

    public List<Report> findByUserIdAndTicketId(UUID userId, UUID ticketId) {
        String sql = "SELECT * FROM reports WHERE user_id = ? AND ticket_id = ?";
        return jdbcTemplate.query(sql, reportRowMapper, userId, ticketId);
    }

    public boolean existsByTicket_IdAndStatusIn(UUID ticketId, List<ReportStatus> statuses) {
        if (statuses == null || statuses.isEmpty()) {
            return false;
        }
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM reports WHERE ticket_id = ? AND status IN (");
        for (int i = 0; i < statuses.size(); i++) {
            sql.append("?");
            if (i < statuses.size() - 1) {
                sql.append(", ");
            }
        }
        sql.append(")");

        Object[] params = new Object[statuses.size() + 1];
        params[0] = ticketId;
        for (int i = 0; i < statuses.size(); i++) {
            params[i + 1] = statuses.get(i).name();
        }

        Long count = jdbcTemplate.queryForObject(sql.toString(), Long.class, params);
        return count != null && count > 0;
    }

    public List<Report> findPendingReportsBySupportId(UUID supportId) {
        String sql = "SELECT * FROM reports WHERE support_id = ? AND status = 'PENDING'";
        return jdbcTemplate.query(sql, reportRowMapper, supportId);
    }

    public List<Report> findInProgressReportsBySupportId(UUID supportId) {
        String sql = "SELECT * FROM reports WHERE support_id = ? AND status = 'IN_PROGRESS'";
        return jdbcTemplate.query(sql, reportRowMapper, supportId);
    }

    public List<Report> findResolvedReportsBySupportId(UUID supportId) {
        String sql = "SELECT * FROM reports WHERE support_id = ? AND status = 'RESOLVED'";
        return jdbcTemplate.query(sql, reportRowMapper, supportId);
    }

    public List<Report> findRejectedReportsBySupportId(UUID supportId) {
        String sql = "SELECT * FROM reports WHERE support_id = ? AND status = 'REJECTED'";
        return jdbcTemplate.query(sql, reportRowMapper, supportId);
    }

    public List<Report> findReopenedReportsBySupportId(UUID supportId) {
        String sql = "SELECT * FROM reports WHERE support_id = ? AND status = 'REOPENED'";
        return jdbcTemplate.query(sql, reportRowMapper, supportId);
    }
}