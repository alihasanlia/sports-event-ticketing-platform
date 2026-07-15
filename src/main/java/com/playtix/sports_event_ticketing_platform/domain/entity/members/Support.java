package com.playtix.sports_event_ticketing_platform.domain.entity.members;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "supports")
public class Support extends BaseUser {

    public Support() {
        this.setRole(Role.SUPPORT);
    }
    
}
