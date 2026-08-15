package com.playtix.sports_event_ticketing_platform.domain.entity.details;

import java.util.UUID;

import com.playtix.sports_event_ticketing_platform.domain.entity.ticket.Ticket;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class BaseDetails {

    @EqualsAndHashCode.Include
    private UUID id = UUID.randomUUID();

    private String tournamentName;

    private String leagueName;

    @Size(max = 500)
    private String facilities;

    private String stadiumName;

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