package com.playtix.sports_event_ticketing_platform.domain.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.playtix.sports_event_ticketing_platform.domain.entity.match.Match;

import jakarta.validation.constraints.NotBlank;
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
public class Team {

    @EqualsAndHashCode.Include
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @NotBlank(message = "Team name is required")
    private String name;

    private String city;

    private String homeStadium;

    private Integer foundedYear;

    private String logo;

    private String coach;

    private String description;

    @Builder.Default
    private List<Match> homeMatches = new ArrayList<>();

    @Builder.Default
    private List<Match> awayMatches = new ArrayList<>();

    public void addHomeMatch(Match match) {
        if (match != null && !homeMatches.contains(match)) {
            homeMatches.add(match);
            match.setHomeTeam(this);
        }
    }

    public void addAwayMatch(Match match) {
        if (match != null && !awayMatches.contains(match)) {
            awayMatches.add(match);
            match.setAwayTeam(this);
        }
    }
    
    public List<Match> getAllMatches() {
        List<Match> allMatches = new ArrayList<>();
        allMatches.addAll(homeMatches);
        allMatches.addAll(awayMatches);
        return allMatches;
    }

    @Override
    public String toString() {
        return "Team{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", coach='" + coach + '\'' +
                '}';
    }
}