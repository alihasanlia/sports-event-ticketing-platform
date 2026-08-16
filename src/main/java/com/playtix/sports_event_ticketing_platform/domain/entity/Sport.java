package com.playtix.sports_event_ticketing_platform.domain.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.playtix.sports_event_ticketing_platform.domain.entity.tournament.Tournament;

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
public class Sport {

    @EqualsAndHashCode.Include
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @NotNull(message = "Sport type is required")
    private SportType name;

    private String description;

    private int numberOfPlayers;

    @Builder.Default
    private List<League> leagues = new ArrayList<>();

    @Builder.Default
    private List<Tournament> tournaments = new ArrayList<>();

    public void addLeague(League league) {
        if (league != null && !leagues.contains(league)) {
            leagues.add(league);
            league.setSport(this);
        }
    }

    public void removeLeague(League league) {
        if (league != null && leagues.remove(league)) {
            league.setSport(null);
        }
    }

    public void addTournament(Tournament tournament) {
        if (tournament != null && !tournaments.contains(tournament)) {
            tournaments.add(tournament);
            tournament.setSport(this);
        }
    }

    public void removeTournament(Tournament tournament) {
        if (tournament != null && tournaments.remove(tournament)) {
            tournament.setSport(null);
        }
    }

    public int getLeaguesCount() {
        return leagues != null ? leagues.size() : 0;
    }

    public int getTournamentsCount() {
        return tournaments != null ? tournaments.size() : 0;
    }

    public boolean hasLeagues() {
        return leagues != null && !leagues.isEmpty();
    }

    public boolean hasTournaments() {
        return tournaments != null && !tournaments.isEmpty();
    }

    @Override
    public String toString() {
        return "Sport{" +
                "id=" + id +
                ", name=" + name +
                ", description='" + description + '\'' +
                ", leaguesCount=" + getLeaguesCount() +
                ", tournamentsCount=" + getTournamentsCount() +
                '}';
    }
}