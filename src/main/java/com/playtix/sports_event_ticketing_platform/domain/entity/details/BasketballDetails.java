package com.playtix.sports_event_ticketing_platform.domain.entity.details;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@DiscriminatorValue("BASKETBALL")
@Table(name = "basketball_details")
public class BasketballDetails extends BaseDetails {
    
}
