package com.playtix.sports_event_ticketing_platform.domain.entity.details;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@DiscriminatorValue("FOOTBALL")
@Table(name = "football_details")
public class FootballDetails extends BaseDetails {
    
}
