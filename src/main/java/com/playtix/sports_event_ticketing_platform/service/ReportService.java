package com.playtix.sports_event_ticketing_platform.service;

import com.playtix.sports_event_ticketing_platform.domain.dto.report.AdminReportDto;
import com.playtix.sports_event_ticketing_platform.domain.dto.report.AssignReportRequest;
import com.playtix.sports_event_ticketing_platform.domain.dto.report.CreateReportRequest;
import com.playtix.sports_event_ticketing_platform.domain.dto.report.UpdateReportRequest;
import com.playtix.sports_event_ticketing_platform.domain.dto.report.UserReportDto;
import com.playtix.sports_event_ticketing_platform.domain.entity.members.Support;
import com.playtix.sports_event_ticketing_platform.domain.entity.members.User;
import com.playtix.sports_event_ticketing_platform.domain.entity.report.Report;
import com.playtix.sports_event_ticketing_platform.domain.entity.report.ReportStatus;
import com.playtix.sports_event_ticketing_platform.domain.entity.report.ReportSubject;
import com.playtix.sports_event_ticketing_platform.domain.entity.ticket.Ticket;
import com.playtix.sports_event_ticketing_platform.domain.entity.ticket.TicketStatus;
import com.playtix.sports_event_ticketing_platform.mapper.ReportMapper;
import com.playtix.sports_event_ticketing_platform.repository.ReportRepository;
import com.playtix.sports_event_ticketing_platform.repository.SupportRepository;
import com.playtix.sports_event_ticketing_platform.repository.TicketRepository;
import com.playtix.sports_event_ticketing_platform.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final SupportRepository supportRepository;
    private final ReportMapper reportMapper;

    @Transactional
    public UserReportDto createReport(CreateReportRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Ticket ticket = ticketRepository.findById(request.ticketId())
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        if (ticket.getStatus() != TicketStatus.SOLD) {
            throw new RuntimeException("You can only report for purchased tickets");
        }

        if (!ticket.getReservation().getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You can only report for your own tickets");
        }

        Report report = new Report();
        report.setSubject(request.subject());
        report.setDescription(request.description());
        report.setStatus(ReportStatus.PENDING);
        report.setUser(user);
        report.setTicket(ticket);
        Support support = supportRepository.findSupportWithFewestActiveReports().orElse(null);
        report.assignToSupport(support);

        report = reportRepository.save(report);
        return reportMapper.toUserReportDto(report);
    }

    @Transactional
    public AdminReportDto assignToSupport(AssignReportRequest request) {
        Report report = reportRepository.findById(request.reportId())
                .orElseThrow(() -> new RuntimeException("Report not found"));

        if (report.getStatus() != ReportStatus.PENDING) {
            throw new RuntimeException("Only pending reports can be assigned");
        }

        Support support = supportRepository.findById(request.supportId())
                .orElseThrow(() -> new RuntimeException("Support not found"));

        report.assignToSupport(support);
        report = reportRepository.save(report);

        return reportMapper.toAdminReportDto(report);
    }

    @Transactional
    public AdminReportDto resolveReport(UpdateReportRequest request) {
        Report report = reportRepository.findById(request.reportId())
                .orElseThrow(() -> new RuntimeException("Report not found"));

        if (report.getStatus() == ReportStatus.RESOLVED) {
            throw new RuntimeException("Report is already resolved");
        }

        if (report.getStatus() == ReportStatus.REJECTED) {
            throw new RuntimeException("Report is already rejected");
        }

        report.resolve(request.adminResponse());
        report = reportRepository.save(report);

        return reportMapper.toAdminReportDto(report);
    }

    @Transactional
    public AdminReportDto rejectReport(UpdateReportRequest request) {
        Report report = reportRepository.findById(request.reportId())
                .orElseThrow(() -> new RuntimeException("Report not found"));

        if (report.getStatus() == ReportStatus.RESOLVED) {
            throw new RuntimeException("Report is already resolved");
        }

        if (report.getStatus() == ReportStatus.REJECTED) {
            throw new RuntimeException("Report is already rejected");
        }

        report.reject(request.adminResponse());
        report = reportRepository.save(report);

        return reportMapper.toAdminReportDto(report);
    }

    @Transactional
    public AdminReportDto reopenReport(UUID reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        if (report.getStatus() != ReportStatus.RESOLVED && report.getStatus() != ReportStatus.REJECTED) {
            throw new RuntimeException("Only resolved or rejected reports can be reopened");
        }

        report.reopen();
        report = reportRepository.save(report);

        return reportMapper.toAdminReportDto(report);
    }

    @Transactional(readOnly = true)
    public UserReportDto getUserReport(UUID reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        return reportMapper.toUserReportDto(report);
    }

    @Transactional(readOnly = true)
    public AdminReportDto getAdminReport(UUID reportId) {
        Report report = reportRepository.findByIdWithAllRelationships(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        return reportMapper.toAdminReportDto(report);
    }

    @Transactional(readOnly = true)
    public List<UserReportDto> getUserReports(UUID userId) {
        List<Report> reports = reportRepository.findByUser_Id(userId);
        return reportMapper.toUserReportDtoList(reports);
    }

    @Transactional(readOnly = true)
    public List<AdminReportDto> getAllReports() {
        List<Report> reports = reportRepository.findAll();
        return reportMapper.toAdminReportDtoList(reports);
    }

    @Transactional(readOnly = true)
    public List<AdminReportDto> getReportsByStatus(ReportStatus status) {
        List<Report> reports = reportRepository.findByStatus(status);
        return reportMapper.toAdminReportDtoList(reports);
    }

    @Transactional(readOnly = true)
    public List<AdminReportDto> getReportsBySubject(ReportSubject subject) {
        List<Report> reports = reportRepository.findBySubject(subject);
        return reportMapper.toAdminReportDtoList(reports);
    }

    @Transactional(readOnly = true)
    public List<AdminReportDto> getPendingReports() {
        List<Report> reports = reportRepository.findPendingReportsOrderByCreatedAt();
        return reportMapper.toAdminReportDtoList(reports);
    }

    @Transactional(readOnly = true)
    public List<AdminReportDto> getUnassignedReports() {
        List<Report> reports = reportRepository.findUnassignedPendingReports();
        return reportMapper.toAdminReportDtoList(reports);
    }

    @Transactional(readOnly = true)
    public List<AdminReportDto> getReportsBySupportId(UUID supportId) {
        List<Report> reports = reportRepository.findBySupport_Id(supportId);
        return reportMapper.toAdminReportDtoList(reports);
    }

    @Transactional(readOnly = true)
    public long countByStatus(ReportStatus status) {
        return reportRepository.countByStatus(status);
    }

    @Transactional(readOnly = true)
    public long countByUser(UUID userId) {
        return reportRepository.countByUser_Id(userId);
    }
}
