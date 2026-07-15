package com.playtix.sports_event_ticketing_platform.domain.entity.report;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "reports")
@Getter
@Setter
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportSubject subject;

    @Column(nullable = false)
    private String description;

    @Column(name = "date", updatable = false, nullable = false)
    private LocalDateTime reportDate;

    @Column(name = "admin_response")
    private String adminResponse;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status;
    
}
