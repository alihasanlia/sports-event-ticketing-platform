package com.playtix.sports_event_ticketing_platform.domain.entity.report;

import java.time.LocalDateTime;
import java.util.UUID;

import com.playtix.sports_event_ticketing_platform.domain.entity.members.Support;
import com.playtix.sports_event_ticketing_platform.domain.entity.members.User;
import com.playtix.sports_event_ticketing_platform.domain.entity.ticket.Ticket;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Report {

    private UUID id = UUID.randomUUID();

    @NotNull
    private ReportSubject subject;

    @NotBlank
    @Size(min = 10, max = 1000)
    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String adminResponse;

    @NotNull
    private ReportStatus status;

    private User user;

    private Support support;

    private Ticket ticket;

    public void initializeOnCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = ReportStatus.PENDING;
        }
    }

    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

    public void setUser(User user) {
        this.user = user;
        if (user != null && user.getReports() != null && !user.getReports().contains(this)) {
            user.getReports().add(this);
        }
    }

    public void assignToSupport(Support support) {
        if (this.support != null && this.support != support && this.support.getReports() != null) {
            this.support.getReports().remove(this);
        }
        this.support = support;
        this.status = ReportStatus.IN_PROGRESS;
        if (support != null && support.getReports() != null && !support.getReports().contains(this)) {
            support.getReports().add(this);
        }
        updateTimestamp();
    }

    public void resolve(String response) {
        if (this.status == ReportStatus.RESOLVED) {
            throw new IllegalStateException("Report is already resolved");
        }
        this.status = ReportStatus.RESOLVED;
        this.adminResponse = response;
        updateTimestamp();
    }

    public void reject(String reason) {
        if (this.status == ReportStatus.RESOLVED) {
            throw new IllegalStateException("Report is already resolved");
        }
        this.status = ReportStatus.REJECTED;
        this.adminResponse = reason;
        updateTimestamp();
    }

    public void reopen() {
        if (this.status != ReportStatus.RESOLVED && this.status != ReportStatus.REJECTED) {
            throw new IllegalStateException("Only resolved or rejected reports can be reopened");
        }
        this.status = ReportStatus.REOPENED;
        updateTimestamp();
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