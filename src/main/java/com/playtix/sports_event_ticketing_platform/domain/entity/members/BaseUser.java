package com.playtix.sports_event_ticketing_platform.domain.entity.members;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public abstract class BaseUser {

    private UUID id;
    private String firstname;
    private String lastname;
    private String email;
    private String phoneNumber;
    private String city;
    private String passwordHash;
    private LocalDateTime registrationDate;
    private AccountStatus status;
    private Role role;

}