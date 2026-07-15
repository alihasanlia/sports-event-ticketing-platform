package com.playtix.sports_event_ticketing_platform.domain.entity.members;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User extends BaseUser {

    public User() {
        this.setRole(Role.USER);
    }
    
}
