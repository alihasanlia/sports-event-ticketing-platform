package com.playtix.sports_event_ticketing_platform.repository;

import com.playtix.sports_event_ticketing_platform.domain.entity.members.AccountStatus;
import com.playtix.sports_event_ticketing_platform.domain.entity.members.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    
    boolean existsByEmail(String email);

    List<User> findByFirstname(String firstname);

    List<User> findByLastname(String lastname);

    List<User> findByCity(String city);

    List<User> findByStatus(AccountStatus status);

    List<User> findByFirstnameAndLastname(String firstname, String lastname);

    Optional<User> findByPhoneNumber(String phoneNumber);

    List<User> findByRegistrationDateAfter(LocalDateTime date);

    List<User> findByRegistrationDateBefore(LocalDateTime date);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.reports WHERE u.id = :userId")
    Optional<User> findByIdWithReports(@Param("userId") UUID userId);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.payments WHERE u.id = :userId")
    Optional<User> findByIdWithPayments(@Param("userId") UUID userId);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.reservedTickets WHERE u.id = :userId")
    Optional<User> findByIdWithReservations(@Param("userId") UUID userId);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.canceledTickets WHERE u.id = :userId")
    Optional<User> findByIdWithCanceledTickets(@Param("userId") UUID userId);

    @Query("SELECT u FROM User u " +
           "LEFT JOIN FETCH u.reports " +
           "LEFT JOIN FETCH u.payments " +
           "LEFT JOIN FETCH u.reservedTickets " +
           "LEFT JOIN FETCH u.canceledTickets " +
           "WHERE u.id = :userId")
    Optional<User> findByIdWithAllRelationships(@Param("userId") UUID userId);

    long countByStatus(AccountStatus status);

    @Query("SELECT u FROM User u WHERE u.status = 'ACTIVE'")
    List<User> findActiveUsers();

}
