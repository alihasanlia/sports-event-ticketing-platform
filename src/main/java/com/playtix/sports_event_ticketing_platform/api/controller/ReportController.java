package com.playtix.sports_event_ticketing_platform.api.controller;

import com.playtix.sports_event_ticketing_platform.domain.dto.report.AdminReportDto;
import com.playtix.sports_event_ticketing_platform.domain.dto.report.AssignReportRequest;
import com.playtix.sports_event_ticketing_platform.domain.dto.report.CreateReportRequest;
import com.playtix.sports_event_ticketing_platform.domain.dto.report.UpdateReportRequest;
import com.playtix.sports_event_ticketing_platform.domain.dto.report.UserReportDto;
import com.playtix.sports_event_ticketing_platform.domain.entity.report.ReportStatus;
import com.playtix.sports_event_ticketing_platform.domain.entity.report.ReportSubject;
import com.playtix.sports_event_ticketing_platform.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<UserReportDto> createReport(@Valid @RequestBody CreateReportRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reportService.createReport(request));
    }

    @PostMapping("/assign")
    public ResponseEntity<AdminReportDto> assignToSupport(@Valid @RequestBody AssignReportRequest request) {
        return ResponseEntity.ok(reportService.assignToSupport(request));
    }

    @PutMapping("/resolve")
    public ResponseEntity<AdminReportDto> resolveReport(@Valid @RequestBody UpdateReportRequest request) {
        return ResponseEntity.ok(reportService.resolveReport(request));
    }

    @PutMapping("/reject")
    public ResponseEntity<AdminReportDto> rejectReport(@Valid @RequestBody UpdateReportRequest request) {
        return ResponseEntity.ok(reportService.rejectReport(request));
    }

    @PostMapping("/{reportId}/reopen")
    public ResponseEntity<AdminReportDto> reopenReport(@PathVariable UUID reportId) {
        return ResponseEntity.ok(reportService.reopenReport(reportId));
    }

    @GetMapping("/{reportId}/user")
    public ResponseEntity<UserReportDto> getUserReport(@PathVariable UUID reportId) {
        return ResponseEntity.ok(reportService.getUserReport(reportId));
    }

    @GetMapping("/{reportId}/admin")
    public ResponseEntity<AdminReportDto> getAdminReport(@PathVariable UUID reportId) {
        return ResponseEntity.ok(reportService.getAdminReport(reportId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserReportDto>> getUserReports(@PathVariable UUID userId) {
        return ResponseEntity.ok(reportService.getUserReports(userId));
    }

    @GetMapping("/admin/all")
    public ResponseEntity<List<AdminReportDto>> getAllReports() {
        return ResponseEntity.ok(reportService.getAllReports());
    }

    @GetMapping("/admin/status/{status}")
    public ResponseEntity<List<AdminReportDto>> getReportsByStatus(@PathVariable ReportStatus status) {
        return ResponseEntity.ok(reportService.getReportsByStatus(status));
    }

    @GetMapping("/admin/subject/{subject}")
    public ResponseEntity<List<AdminReportDto>> getReportsBySubject(@PathVariable ReportSubject subject) {
        return ResponseEntity.ok(reportService.getReportsBySubject(subject));
    }

    @GetMapping("/admin/pending")
    public ResponseEntity<List<AdminReportDto>> getPendingReports() {
        return ResponseEntity.ok(reportService.getPendingReports());
    }

    @GetMapping("/admin/unassigned")
    public ResponseEntity<List<AdminReportDto>> getUnassignedReports() {
        return ResponseEntity.ok(reportService.getUnassignedReports());
    }

    @GetMapping("/admin/support/{supportId}")
    public ResponseEntity<List<AdminReportDto>> getReportsBySupportId(@PathVariable UUID supportId) {
        return ResponseEntity.ok(reportService.getReportsBySupportId(supportId));
    }

    @GetMapping("/count/status/{status}")
    public ResponseEntity<Long> countByStatus(@PathVariable ReportStatus status) {
        return ResponseEntity.ok(reportService.countByStatus(status));
    }

    @GetMapping("/count/user/{userId}")
    public ResponseEntity<Long> countByUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(reportService.countByUser(userId));
    }
}