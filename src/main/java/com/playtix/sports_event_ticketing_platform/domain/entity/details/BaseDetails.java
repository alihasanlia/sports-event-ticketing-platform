package com.playtix.sports_event_ticketing_platform.domain.entity.details;

import java.util.UUID;

import com.playtix.sports_event_ticketing_platform.domain.entity.ticket.Ticket;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "sport_type")
public abstract class BaseDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id = UUID.randomUUID();

    @Column(name = "tournament_name", updatable = false)
    private String tournamentName;

    @Column(name = "league_name", updatable = false)
    private String leagueName;

    @Size(max = 500)
    @Column(updatable = false, length = 500)
    private String facilities;

    @Column(name = "stadium_name", length = 100)
    private String stadiumName;

    @OneToOne(mappedBy = "baseDetails", fetch = FetchType.LAZY)
    private Ticket ticket;
    
    public String getCompetitionName() {
        if (tournamentName != null && !tournamentName.isEmpty()) {
            return "Tournament: " + tournamentName;
        }
        if (leagueName != null && !leagueName.isEmpty()) {
            return "League: " + leagueName;
        }
        return "No competition";
    }

    public boolean hasTournament() {
        return tournamentName != null && !tournamentName.isEmpty();
    }

    public boolean hasLeague() {
        return leagueName != null && !leagueName.isEmpty();
    }

    public boolean hasFacilities() {
        return facilities != null && !facilities.isEmpty();
    }

    public String getFullDetails() {
        StringBuilder sb = new StringBuilder();
        sb.append("Match Details: ");
        sb.append(getCompetitionName());
        if (stadiumName != null) {
            sb.append(", Stadium: ").append(stadiumName);
        }
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return "BaseDetails{" +
                "id=" + id +
                ", tournamentName='" + tournamentName + '\'' +
                ", leagueName='" + leagueName + '\'' +
                ", stadiumName='" + stadiumName + '\'' +
                '}';
    }
}