package com.playtix.sports_event_ticketing_platform.domain.entity.details;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "sport_type")
public abstract class BaseDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tournoment_name", nullable = false, updatable = false)
    private String tournomentName;

    @Column(name = "league_name", nullable = false, updatable = false)
    private String leagueName;

    @Column(updatable = false)
    private String facilities;

}
