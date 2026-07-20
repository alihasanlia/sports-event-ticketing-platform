package com.playtix.sports_event_ticketing_platform.domain.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.playtix.sports_event_ticketing_platform.domain.entity.match.Match;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "leagues")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class League {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @NotBlank(message = "League name is required")
    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 50)
    private String country;

    @Column(length = 20)
    private String season;

    @Column(name = "number_of_teams")
    private Integer numberOfTeams;

    @Column(length = 500)
    private String description;

    @NotNull(message = "Sport is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sport_id", nullable = false)
    private Sport sport;

    @OneToMany(mappedBy = "league", cascade = CascadeType.ALL, orphanRemoval = true)
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