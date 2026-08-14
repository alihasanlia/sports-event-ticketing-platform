package com.playtix.sports_event_ticketing_platform.repository;

import com.playtix.sports_event_ticketing_platform.domain.entity.members.AccountStatus;
import com.playtix.sports_event_ticketing_platform.domain.entity.members.Role;
import com.playtix.sports_event_ticketing_platform.domain.entity.members.Support;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class SupportRepository {

    private final JdbcTemplate jdbcTemplate;

    public SupportRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Support> supportRowMapper = (rs, rowNum) -> {
        Support support = new Support();
        support.setId(UUID.fromString(rs.getString("id")));
        support.setFirstname(rs.getString("firstname"));
        support.setLastname(rs.getString("lastname"));
        support.setEmail(rs.getString("email"));
        support.setPhoneNumber(rs.getString("phone_number"));
        support.setCity(rs.getString("city"));
        support.setPasswordHash(rs.getString("password_hash"));
        
        Timestamp regDate = rs.getTimestamp("registration_date");
        if (regDate != null) {
            support.setRegistrationDate(regDate.toLocalDateTime());
        }
        
        String statusStr = rs.getString("status");
        if (statusStr != null) {
            support.setStatus(AccountStatus.valueOf(statusStr));
        }
        
        String roleStr = rs.getString("role");
        if (roleStr != null) {
            support.setRole(Role.valueOf(roleStr));
        }
        
        return support;
    };

    // --- Standard CRUD Methods ---

    public List<Support> findAll() {
        String sql = "SELECT * FROM supports";
        return jdbcTemplate.query(sql, supportRowMapper);
    }

    public Optional<Support> findById(UUID id) {
        String sql = "SELECT * FROM supports WHERE id = ?";
        List<Support> results = jdbcTemplate.query(sql, supportRowMapper, id);
        return results.stream().findFirst();
    }

    public Support save(Support support) {
        if (support.getId() != null && findById(support.getId()).isPresent()) {
            String sql = "UPDATE supports SET firstname = ?, lastname = ?, email = ?, phone_number = ?, city = ?, password_hash = ?, registration_date = ?, status = ?, role = ? WHERE id = ?";
            if (support.getRegistrationDate() == null) {
                support.setRegistrationDate(LocalDateTime.now());
            }
            jdbcTemplate.update(sql,
                    support.getFirstname(),
                    support.getLastname(),
                    support.getEmail(),
                    support.getPhoneNumber(),
                    support.getCity(),
                    support.getPasswordHash(),
                    Timestamp.valueOf(support.getRegistrationDate()),
                    support.getStatus() != null ? support.getStatus().name() : null,
                    support.getRole() != null ? support.getRole().name() : null,
                    support.getId()
            );
        } else {
            String sql = "INSERT INTO supports (id, firstname, lastname, email, phone_number, city, password_hash, registration_date, status, role) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            if (support.getId() == null) {
                support.setId(UUID.randomUUID());
            }
            if (support.getRegistrationDate() == null) {
                support.setRegistrationDate(LocalDateTime.now());
            }
            jdbcTemplate.update(sql,
                    support.getId(),
                    support.getFirstname(),
                    support.getLastname(),
                    support.getEmail(),
                    support.getPhoneNumber(),
                    support.getCity(),
                    support.getPasswordHash(),
                    Timestamp.valueOf(support.getRegistrationDate()),
                    support.getStatus() != null ? support.getStatus().name() : null,
                    support.getRole() != null ? support.getRole().name() : null
            );
        }
        return support;
    }

    public void deleteById(UUID id) {
        String sql = "DELETE FROM supports WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public void delete(Support support) {
        if (support != null && support.getId() != null) {
            deleteById(support.getId());
        }
    }

    public long count() {
        String sql = "SELECT COUNT(*) FROM supports";
        Long count = jdbcTemplate.queryForObject(sql, Long.class);
        return count != null ? count : 0L;
    }

    public Optional<Support> findByEmail(String email) {
        String sql = "SELECT * FROM supports WHERE email = ?";
        List<Support> results = jdbcTemplate.query(sql, supportRowMapper, email);
        return results.stream().findFirst();
    }

    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(id) FROM supports WHERE email = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, email);
        return count != null && count > 0;
    }

    public List<Support> findByFirstname(String firstname) {
        String sql = "SELECT * FROM supports WHERE firstname = ?";
        return jdbcTemplate.query(sql, supportRowMapper, firstname);
    }

    public List<Support> findByLastname(String lastname) {
        String sql = "SELECT * FROM supports WHERE lastname = ?";
        return jdbcTemplate.query(sql, supportRowMapper, lastname);
    }

    public List<Support> findByCity(String city) {
        String sql = "SELECT * FROM supports WHERE city = ?";
        return jdbcTemplate.query(sql, supportRowMapper, city);
    }

    public List<Support> findByStatus(AccountStatus status) {
        String sql = "SELECT * FROM supports WHERE status = ?";
        return jdbcTemplate.query(sql, supportRowMapper, status.name());
    }

    public List<Support> findByFirstnameAndLastname(String firstname, String lastname) {
        String sql = "SELECT * FROM supports WHERE firstname = ? AND lastname = ?";
        return jdbcTemplate.query(sql, supportRowMapper, firstname, lastname);
    }

    public Optional<Support> findSupportWithFewestActiveReports() {
        String sql = "SELECT s.* FROM supports s " +
                    "LEFT JOIN reports r ON s.id = r.support_id AND r.status NOT IN ('REJECTED', 'RESOLVED') " +
                    "WHERE s.status = 'ACTIVE' " +
                    "GROUP BY s.id, s.firstname, s.lastname, s.email, s.phone_number, " +
                    "         s.city, s.password_hash, s.registration_date, s.status, s.role " +
                    "ORDER BY COUNT(r.id) ASC " +
                    "LIMIT 1";
                    
        List<Support> results = jdbcTemplate.query(sql, supportRowMapper);
        return results.stream().findFirst();
    }

    public Optional<Support> findByPhoneNumber(String phoneNumber) {
        String sql = "SELECT * FROM supports WHERE phone_number = ?";
        List<Support> results = jdbcTemplate.query(sql, supportRowMapper, phoneNumber);
        return results.stream().findFirst();
    }

    public List<Support> findByRegistrationDateAfter(LocalDateTime date) {
        String sql = "SELECT * FROM supports WHERE registration_date > ?";
        return jdbcTemplate.query(sql, supportRowMapper, Timestamp.valueOf(date));
    }

    public List<Support> findByRegistrationDateBefore(LocalDateTime date) {
        String sql = "SELECT * FROM supports WHERE registration_date < ?";
        return jdbcTemplate.query(sql, supportRowMapper, Timestamp.valueOf(date));
    }

    public Optional<Support> findByIdWithReports(UUID supportId) {
        return findById(supportId);
    }

    public List<Support> findActiveSupports() {
        String sql = "SELECT * FROM supports WHERE status = 'ACTIVE'";
        return jdbcTemplate.query(sql, supportRowMapper);
    }

    public List<Support> findSupportsWithPendingReportsGreaterThan(long threshold) {
        String sql = "SELECT s.* FROM supports s WHERE (SELECT COUNT(r.id) FROM reports r WHERE r.support_id = s.id AND r.status = 'PENDING') > ?";
        return jdbcTemplate.query(sql, supportRowMapper, threshold);
    }

    public List<Support> findSupportsWithNoPendingReports() {
        String sql = "SELECT s.* FROM supports s WHERE (SELECT COUNT(r.id) FROM reports r WHERE r.support_id = s.id AND r.status = 'PENDING') = 0";
        return jdbcTemplate.query(sql, supportRowMapper);
    }

    public List<Support> findAllOrderByReportsCountDesc() {
        String sql = "SELECT s.* FROM supports s LEFT JOIN reports r ON s.id = r.support_id GROUP BY s.id, s.firstname, s.lastname, s.email, s.phone_number, s.city, s.password_hash, s.registration_date, s.status, s.role ORDER BY COUNT(r.id) DESC";
        return jdbcTemplate.query(sql, supportRowMapper);
    }

    public List<Object[]> findSupportsWithReportCount() {
        String sql = "SELECT s.id, COUNT(r.id) FROM supports s LEFT JOIN reports r ON s.id = r.support_id GROUP BY s.id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Object[]{
                findById(UUID.fromString(rs.getString(1))).orElse(null),
                rs.getLong(2)
        });
    }

    public List<Support> findAvailableSupports() {
        String sql = "SELECT s.* FROM supports s WHERE s.status = 'ACTIVE' AND (SELECT COUNT(r.id) FROM reports r WHERE r.support_id = s.id AND r.status = 'PENDING') <= 5";
        return jdbcTemplate.query(sql, supportRowMapper);
    }

    public List<Support> findByStatusOrderByRegistrationDateDesc(AccountStatus status) {
        String sql = "SELECT * FROM supports WHERE status = ? ORDER BY registration_date DESC";
        return jdbcTemplate.query(sql, supportRowMapper, status.name());
    }

    public long countByStatus(AccountStatus status) {
        String sql = "SELECT COUNT(id) FROM supports WHERE status = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, status.name());
        return count != null ? count : 0L;
    }

    public long countPendingReportsBySupportId(UUID supportId) {
        String sql = "SELECT COUNT(id) FROM reports WHERE support_id = ? AND status = 'PENDING'";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, supportId);
        return count != null ? count : 0L;
    }

    public List<Support> findSupportsWithResolvedReports() {
        String sql = "SELECT DISTINCT s.* FROM supports s JOIN reports r ON s.id = r.support_id WHERE r.status = 'RESOLVED'";
        return jdbcTemplate.query(sql, supportRowMapper);
    }

    public List<Support> findSupportsWithMaxReports() {
        String sql = "SELECT s.* FROM supports s LEFT JOIN reports r ON s.id = r.support_id GROUP BY s.id, s.firstname, s.lastname, s.email, s.phone_number, s.city, s.password_hash, s.registration_date, s.status, s.role HAVING COUNT(r.id) = (SELECT MAX(cnt) FROM (SELECT COUNT(r2.id) as cnt FROM supports s2 LEFT JOIN reports r2 ON s2.id = r2.support_id GROUP BY s2.id) t)";
        return jdbcTemplate.query(sql, supportRowMapper);
    }
}