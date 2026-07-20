package com.playtix.sports_event_ticketing_platform.domain.entity.match;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.playtix.sports_event_ticketing_platform.domain.entity.League;
import com.playtix.sports_event_ticketing_platform.domain.entity.Stadium;
import com.playtix.sports_event_ticketing_platform.domain.entity.Team;
import com.playtix.sports_event_ticketing_platform.domain.entity.SportType;
import com.playtix.sports_event_ticketing_platform.domain.entity.ticket.TicketCategory;
import com.playtix.sports_event_ticketing_platform.domain.entity.tournament.Tournament;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "matches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @Column(name = "match_date")
    private LocalDateTime matchDate;

    @Column(name = "match_time")
    private LocalDateTime matchTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "sport_type", nullable = false)
    private SportType sportType;

    @NotNull(message = "Home team is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "home_team_id", nullable = false)
    private Team homeTeam;

    @NotNull(message = "Away team is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "away_team_id", nullable = false)
    private Team awayTeam;

    @NotNull(message = "Stadium is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stadium_id", nullable = false)
    private Stadium stadium;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "league_id")
    private League league;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;

    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TicketCategory> ticketCategories = new ArrayList<>();
    
    public void addTicketCategory(TicketCategory category) {
        if (category != null && !ticketCategories.contains(category)) {
            ticketCategories.add(category);
            category.setMatch(this);
        }
    }

    public void removeTicketCategory(TicketCategory category) {
        if (category != null && ticketCategories.remove(category)) {
            category.setMatch(null);
        }
    }

    public boolean isFinished() {
        return matchDate != null && matchDate.isBefore(LocalDateTime.now());
    }

    public boolean isUpcoming() {
        return matchDate != null && matchDate.isAfter(LocalDateTime.now());
    }

    public String getMatchInfo() {
        return String.format("%s vs %s - %s - %s",
            homeTeam != null ? homeTeam.getName() : "TBD",
            awayTeam != null ? awayTeam.getName() : "TBD",
            matchDate != null ? matchDate.toLocalDate() : "TBD",
            stadium != null ? stadium.getName() : "TBD"
        );
    }
    
    @Override
    public String toString() {
        return "Match{" +
                "id=" + id +
                ", matchDate=" + matchDate +
                ", homeTeam=" + (homeTeam != null ? homeTeam.getName() : "null") +
                ", awayTeam=" + (awayTeam != null ? awayTeam.getName() : "null") +
                ", stadium=" + (stadium != null ? stadium.getName() : "null") +
                '}';
    }
}