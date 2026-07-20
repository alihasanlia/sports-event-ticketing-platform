package com.playtix.sports_event_ticketing_platform.domain.entity;

import java.util.List;
import java.util.UUID;

import com.playtix.sports_event_ticketing_platform.domain.entity.match.Match;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "stadiums")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stadium {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @NotBlank(message = "Stadium name is required")
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank(message = "City is required")
    @Column(nullable = false, length = 50, updatable = false)
    private String city;

    @Positive(message = "Capacity must be positive")
    @Column(nullable = false)
    private int capacity;

    @Column(name = "address")
    private String address;

    @OneToMany(mappedBy = "stadium")
    private List<Match> matches;

    public void addMatch(Match match) {
        if (match != null && !matches.contains(match)) {
            matches.add(match);
            match.setStadium(this);
        }
    }

    public void removeMatch(Match match) {
        if (match != null && matches.remove(match)) {
            match.setStadium(null);
        }
    }

    public String getFullInfo() {
        return String.format("%s (%s) - Capacity: %d", name, city, capacity);
    }

    @Override
    public String toString() {
        return "Stadium{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", capacity=" + capacity +
                '}';
    }

}