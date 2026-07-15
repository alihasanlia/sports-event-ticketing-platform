package com.playtix.sports_event_ticketing_platform.domain.entity.details;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@DiscriminatorValue("VOLLEYBALL")
@Table(name = "volleyball_details")
public class VolleyballDetails extends BaseDetails {
    
}
