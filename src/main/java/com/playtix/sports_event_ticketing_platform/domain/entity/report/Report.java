package com.playtix.sports_event_ticketing_platform.domain.entity.report;

import java.time.LocalDateTime;
import java.util.UUID;

import com.playtix.sports_event_ticketing_platform.domain.entity.members.Support;
import com.playtix.sports_event_ticketing_platform.domain.entity.members.User;
import com.playtix.sports_event_ticketing_platform.domain.entity.ticket.Ticket;

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
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportSubject subject;

    @NotBlank
    @Size(min = 10, max = 1000)
    @Column(nullable = false, length = 1000)
    private String description;

    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "admin_response", length = 500)
    private String adminResponse;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "support_id")
    private Support support;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = ReportStatus.PENDING;
        }
    }

    @PreUpdate
    private void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void setUser(User user) {
        this.user = user;
        if (user != null && !user.getReports().contains(this)) {
            user.getReports().add(this);
        }
    }

    public void assignToSupport(Support support) {
        if (this.support != null && this.support != support) {
            this.support.getReports().remove(this);
        }
        this.support = support;
        this.status = ReportStatus.IN_PROGRESS;
        if (support != null && !support.getReports().contains(this)) {
            support.getReports().add(this);
        }
    }

    public void resolve(String response) {
        if (this.status == ReportStatus.RESOLVED) {
            throw new IllegalStateException("Report is already resolved");
        }
        this.status = ReportStatus.RESOLVED;
        this.adminResponse = response;
    }

    public void reject(String reason) {
        if (this.status == ReportStatus.RESOLVED) {
            throw new IllegalStateException("Report is already resolved");
        }
        this.status = ReportStatus.REJECTED;
        this.adminResponse = reason;
    }

    public void reopen() {
        if (this.status != ReportStatus.RESOLVED && this.status != ReportStatus.REJECTED) {
            throw new IllegalStateException("Only resolved or rejected reports can be reopened");
        }
        this.status = ReportStatus.REOPENED;
    }

    public boolean isPending() {
        return this.status == ReportStatus.PENDING;
    }

    public boolean isResolved() {
        return this.status == ReportStatus.RESOLVED;
    }

    public boolean isInProgress() {
        return this.status == ReportStatus.IN_PROGRESS;
    }

    @Override
    public String toString() {
        return "Report{" +
                "id=" + id +
                ", subject=" + subject +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", user=" + (user != null ? user.getEmail() : "null") +
                ", support=" + (support != null ? support.getEmail() : "unassigned") +
                '}';
    }
}