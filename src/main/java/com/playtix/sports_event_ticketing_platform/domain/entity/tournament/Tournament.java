package com.playtix.sports_event_ticketing_platform.domain.entity.tournament;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.playtix.sports_event_ticketing_platform.domain.entity.Sport;
import com.playtix.sports_event_ticketing_platform.domain.entity.match.Match;

import jakarta.validation.constraints.Future;
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
public class Tournament {

    @EqualsAndHashCode.Include
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @NotBlank(message = "Tournament name is required")
    private String name;

    private String description;

    @Future(message = "Start date must be in the future")
    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @Builder.Default
    private TournamentStatus status = TournamentStatus.UPCOMING;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    
    @Builder.Default
    private List<Match> matches = new ArrayList<>();

    @NotNull(message = "Sport is required")
    private Sport sport;
    
    public void initializeDefaults() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = TournamentStatus.UPCOMING;
        }
    }

    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }
    
    public void addMatch(Match match) {
        if (match != null && !matches.contains(match)) {
            matches.add(match);
            match.setTournament(this);
        }
    }

    public void removeMatch(Match match) {
        if (match != null && matches.remove(match)) {
            match.setTournament(null);
        }
    }
    
    public boolean isActive() {
        return this.status == TournamentStatus.ACTIVE;
    }

    public boolean isFinished() {
        return this.status == TournamentStatus.FINISHED;
    }

    public boolean isUpcoming() {
        return this.status == TournamentStatus.UPCOMING;
    }

    public boolean isCancelled() {
        return this.status == TournamentStatus.CANCELLED;
    }

    public boolean canAddMatch() {
        return this.status == TournamentStatus.UPCOMING || 
               this.status == TournamentStatus.ACTIVE;
    }

    public void start() {
        if (this.status != TournamentStatus.UPCOMING) {
            throw new IllegalStateException("Only upcoming tournaments can be started");
        }
        this.status = TournamentStatus.ACTIVE;
        this.startDate = LocalDateTime.now();
        updateTimestamp();
    }

    public void finish() {
        if (this.status != TournamentStatus.ACTIVE) {
            throw new IllegalStateException("Only active tournaments can be finished");
        }
        this.status = TournamentStatus.FINISHED;
        this.endDate = LocalDateTime.now();
        updateTimestamp();
    }

    public void cancel() {
        if (this.status == TournamentStatus.FINISHED) {
            throw new IllegalStateException("Cannot cancel a finished tournament");
        }
        this.status = TournamentStatus.CANCELLED;
        updateTimestamp();
    }

    public int getMatchesCount() {
        return matches != null ? matches.size() : 0;
    }
    
    @Override
    public String toString() {
        return "Tournament{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", sport=" + (sport != null ? sport.getName() : "null") +
                '}';
    }
}