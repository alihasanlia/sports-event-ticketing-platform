package com.playtix.sports_event_ticketing_platform.domain.entity.members;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.playtix.sports_event_ticketing_platform.domain.entity.report.Report;
import com.playtix.sports_event_ticketing_platform.domain.entity.report.ReportStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Support extends BaseUser {

    public Support() {
        this.setRole(Role.SUPPORT);
        this.reports = new ArrayList<>();
    }

    private List<Report> reports;

    public void addReport(Report report) {
        if (report != null) {
            if (report.getSupport() != null && !report.getSupport().equals(this)) {
                report.getSupport().getReports().remove(report);
            }
            reports.add(report);
            report.setSupport(this);
        }
    }

    public void removeReport(Report report) {
        if (report != null && reports.remove(report)) {
            report.setSupport(null);
        }
    }

    public long getPendingReportsCount() {
        return reports.stream()
                .filter(r -> r.getStatus() == ReportStatus.PENDING)
                .count();
    }

    public List<Report> getResolvedReports() {
        return reports.stream()
                .filter(r -> r.getStatus() == ReportStatus.RESOLVED)
                .collect(Collectors.toList());
    }

    public List<Report> getPendingReports() {
        return reports.stream()
                .filter(r -> r.getStatus() == ReportStatus.PENDING)
                .collect(Collectors.toList());
    }

    public boolean isBusy() {
        return getPendingReportsCount() > 5;
    }

    public boolean hasReport(Report report) {
        return reports.contains(report);
    }

    @Override
    public String toString() {
        return "Support{" +
                "id=" + getId() +
                ", firstname='" + getFirstname() + '\'' +
                ", lastname='" + getLastname() + '\'' +
                ", phone='" + getPhoneNumber() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", reportsCount=" + (reports != null ? reports.size() : 0) +
                ", pendingReports=" + getPendingReportsCount() +
                '}';
    }
}