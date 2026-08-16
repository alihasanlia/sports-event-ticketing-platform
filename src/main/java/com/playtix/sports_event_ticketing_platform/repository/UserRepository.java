package com.playtix.sports_event_ticketing_platform.repository;

import com.playtix.sports_event_ticketing_platform.domain.entity.members.AccountStatus;
import com.playtix.sports_event_ticketing_platform.domain.entity.members.Role;
import com.playtix.sports_event_ticketing_platform.domain.entity.members.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<User> userRowMapper = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getObject("id", UUID.class));
        user.setFirstname(rs.getString("firstname"));
        user.setLastname(rs.getString("lastname"));
        user.setEmail(rs.getString("email"));
        user.setPhoneNumber(rs.getString("phone_number"));
        user.setCity(rs.getString("city"));
        user.setPasswordHash(rs.getString("password_hash"));
        Timestamp regDateTs = rs.getTimestamp("registration_date");
        if (regDateTs != null) {
            user.setRegistrationDate(regDateTs.toLocalDateTime());
        }
        String statusStr = rs.getString("status");
        if (statusStr != null) {
            user.setStatus(AccountStatus.valueOf(statusStr));
        }
        String roleStr = rs.getString("role");
        if (roleStr != null) {
            user.setRole(Role.valueOf(roleStr));
        }
        java.math.BigDecimal balance = rs.getBigDecimal("balance");
        if (balance != null) {
            user.setBalance(balance);
        }
        return user;
    };

    public Optional<User> findById(UUID id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        List<User> results = jdbcTemplate.query(sql, userRowMapper, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, userRowMapper);
    }

    public User save(User user) {
        if (user.getId() == null) {
            user.setId(UUID.randomUUID());
            if (user.getRegistrationDate() == null) {
                user.setRegistrationDate(LocalDateTime.now());
            }
            if (user.getRole() == null) {
                user.setRole(Role.USER);
            }
            if (user.getBalance() == null) {
                user.setBalance(java.math.BigDecimal.ZERO);
            }
            String sql = "INSERT INTO users (id, firstname, lastname, email, phone_number, city, password_hash, registration_date, status, role, balance) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(sql,
                    user.getId(),
                    user.getFirstname(),
                    user.getLastname(),
                    user.getEmail(),
                    user.getPhoneNumber(),
                    user.getCity(),
                    user.getPasswordHash(),
                    user.getRegistrationDate() != null ? Timestamp.valueOf(user.getRegistrationDate()) : null,
                    user.getStatus() != null ? user.getStatus().name() : null,
                    user.getRole() != null ? user.getRole().name() : null,
                    user.getBalance() != null ? user.getBalance() : java.math.BigDecimal.ZERO
            );
        } else {
            String sql = "UPDATE users SET firstname = ?, lastname = ?, email = ?, phone_number = ?, city = ?, password_hash = ?, registration_date = ?, status = ?, role = ?, balance = ? WHERE id = ?";
            jdbcTemplate.update(sql,
                    user.getFirstname(),
                    user.getLastname(),
                    user.getEmail(),
                    user.getPhoneNumber(),
                    user.getCity(),
                    user.getPasswordHash(),
                    user.getRegistrationDate() != null ? Timestamp.valueOf(user.getRegistrationDate()) : null,
                    user.getStatus() != null ? user.getStatus().name() : null,
                    user.getRole() != null ? user.getRole().name() : null,
                    user.getBalance() != null ? user.getBalance() : java.math.BigDecimal.ZERO,
                    user.getId()
            );
        }
        return user;
    }

    public boolean existsById(UUID id) {
        String sql = "SELECT COUNT(*) FROM users WHERE id = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, id);
        return count != null && count > 0;
    }

    public long count() {
        String sql = "SELECT COUNT(*) FROM users";
        Long count = jdbcTemplate.queryForObject(sql, Long.class);
        return count != null ? count : 0;
    }

    public void deleteById(UUID id) {
        String sql = "DELETE FROM users WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public void delete(User user) {
        if (user != null && user.getId() != null) {
            deleteById(user.getId());
        }
    }

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        List<User> results = jdbcTemplate.query(sql, userRowMapper, email);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, email);
        return count != null && count > 0;
    }

    public List<User> findByFirstname(String firstname) {
        String sql = "SELECT * FROM users WHERE firstname = ?";
        return jdbcTemplate.query(sql, userRowMapper, firstname);
    }

    public List<User> findByLastname(String lastname) {
        String sql = "SELECT * FROM users WHERE lastname = ?";
        return jdbcTemplate.query(sql, userRowMapper, lastname);
    }

    public List<User> findByCity(String city) {
        String sql = "SELECT * FROM users WHERE city = ?";
        return jdbcTemplate.query(sql, userRowMapper, city);
    }

    public List<User> findByStatus(AccountStatus status) {
        String sql = "SELECT * FROM users WHERE status = ?";
        return jdbcTemplate.query(sql, userRowMapper, status.name());
    }

    public List<User> findByFirstnameAndLastname(String firstname, String lastname) {
        String sql = "SELECT * FROM users WHERE firstname = ? AND lastname = ?";
        return jdbcTemplate.query(sql, userRowMapper, firstname, lastname);
    }

    public Optional<User> findByPhoneNumber(String phoneNumber) {
        String sql = "SELECT * FROM users WHERE phone_number = ?";
        List<User> results = jdbcTemplate.query(sql, userRowMapper, phoneNumber);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public List<User> findByRegistrationDateAfter(LocalDateTime date) {
        String sql = "SELECT * FROM users WHERE registration_date > ?";
        return jdbcTemplate.query(sql, userRowMapper, Timestamp.valueOf(date));
    }

    public List<User> findByRegistrationDateBefore(LocalDateTime date) {
        String sql = "SELECT * FROM users WHERE registration_date < ?";
        return jdbcTemplate.query(sql, userRowMapper, Timestamp.valueOf(date));
    }

    public Optional<User> findByIdWithReports(UUID userId) {
        String sql = "SELECT * FROM users WHERE id = ?";
        List<User> results = jdbcTemplate.query(sql, userRowMapper, userId);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public Optional<User> findByIdWithPayments(UUID userId) {
        String sql = "SELECT * FROM users WHERE id = ?";
        List<User> results = jdbcTemplate.query(sql, userRowMapper, userId);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public Optional<User> findByIdWithReservations(UUID userId) {
        String sql = "SELECT * FROM users WHERE id = ?";
        List<User> results = jdbcTemplate.query(sql, userRowMapper, userId);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public Optional<User> findByIdWithCanceledTickets(UUID userId) {
        String sql = "SELECT * FROM users WHERE id = ?";
        List<User> results = jdbcTemplate.query(sql, userRowMapper, userId);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public Optional<User> findByIdWithAllRelationships(UUID userId) {
        String sql = "SELECT * FROM users WHERE id = ?";
        List<User> results = jdbcTemplate.query(sql, userRowMapper, userId);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public long countByStatus(AccountStatus status) {
        String sql = "SELECT COUNT(*) FROM users WHERE status = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, status.name());
        return count != null ? count : 0;
    }

    public List<User> findActiveUsers() {
        String sql = "SELECT * FROM users WHERE status = 'ACTIVE'";
        return jdbcTemplate.query(sql, userRowMapper);
    }
    
    public int updateBalance(UUID userId, java.math.BigDecimal newBalance) {
        String sql = "UPDATE users SET balance = ? WHERE id = ?";
        return jdbcTemplate.update(sql, newBalance, userId);
    }

    public int addToBalance(UUID userId, java.math.BigDecimal amount) {
        String sql = "UPDATE users SET balance = balance + ? WHERE id = ?";
        return jdbcTemplate.update(sql, amount, userId);
    }

    public int subtractFromBalance(UUID userId, java.math.BigDecimal amount) {
        String sql = "UPDATE users SET balance = balance - ? WHERE id = ? AND balance >= ?";
        return jdbcTemplate.update(sql, amount, userId, amount);
    }

    public List<User> findByBalanceGreaterThan(java.math.BigDecimal amount) {
        String sql = "SELECT * FROM users WHERE balance > ?";
        return jdbcTemplate.query(sql, userRowMapper, amount);
    }

    public List<User> findByBalanceLessThan(java.math.BigDecimal amount) {
        String sql = "SELECT * FROM users WHERE balance < ?";
        return jdbcTemplate.query(sql, userRowMapper, amount);
    }

}