package com.playtix.sports_event_ticketing_platform.domain.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.playtix.sports_event_ticketing_platform.domain.entity.match.Match;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "teams")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @NotBlank(message = "Team name is required")
    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 50)
    private String city;

    @Column(name = "stadium", length = 100)
    private String homeStadium;

    @Column(name = "founded_year")
    private Integer foundedYear;

    @Column(length = 200)
    private String logo;

    @Column(length = 50)
    private String coach;

    @Column(length = 500)
    private String description;

    @OneToMany(mappedBy = "homeTeam", cascade = CascadeType.PERSIST)
    @Builder.Default
    private List<Match> homeMatches = new ArrayList<>();

    @OneToMany(mappedBy = "awayTeam", cascade = CascadeType.PERSIST)
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
