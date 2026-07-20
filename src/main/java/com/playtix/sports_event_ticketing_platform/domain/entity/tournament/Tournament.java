package com.playtix.sports_event_ticketing_platform.domain.entity.tournament;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.playtix.sports_event_ticketing_platform.domain.entity.Sport;
import com.playtix.sports_event_ticketing_platform.domain.entity.match.Match;

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
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tournaments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @NotBlank(message = "Tournament name is required")
    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Future(message = "Start date must be in the future")
    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TournamentStatus status = TournamentStatus.UPCOMING;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Match> matches = new ArrayList<>();

    @NotNull(message = "Sport is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sport_id", nullable = false)
    private Sport sport;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = TournamentStatus.UPCOMING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
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
    }

    public void finish() {
        if (this.status != TournamentStatus.ACTIVE) {
            throw new IllegalStateException("Only active tournaments can be finished");
        }
        this.status = TournamentStatus.FINISHED;
        this.endDate = LocalDateTime.now();
    }

    public void cancel() {
        if (this.status == TournamentStatus.FINISHED) {
            throw new IllegalStateException("Cannot cancel a finished tournament");
        }
        this.status = TournamentStatus.CANCELLED;
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