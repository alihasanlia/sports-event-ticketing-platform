package com.playtix.sports_event_ticketing_platform.domain.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.playtix.sports_event_ticketing_platform.domain.entity.match.Match;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class League {

    @EqualsAndHashCode.Include
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @NotBlank(message = "League name is required")
    private String name;

    private String country;

    private String season;

    private Integer numberOfTeams;

    private String description;

    @NotNull(message = "Sport is required")
    private Sport sport;

    @Builder.Default
    private List<Match> matches = new ArrayList<>();
    
    public void addMatch(Match match) {
        if (match != null && !matches.contains(match)) {
            matches.add(match);
            match.setLeague(this);
        }
    }

    public void removeMatch(Match match) {
        if (match != null && matches.remove(match)) {
            match.setLeague(null);
        }
    }
    
    public int getMatchesCount() {
        return matches != null ? matches.size() : 0;
    }

    public String getFullName() {
        return country != null ? country + " - " + name : name;
    }
    
    @Override
    public String toString() {
        return "League{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", country='" + country + '\'' +
                ", season='" + season + '\'' +
                ", sport=" + (sport != null ? sport.getName() : "null") +
                ", matchesCount=" + getMatchesCount() +
                '}';
    }
}