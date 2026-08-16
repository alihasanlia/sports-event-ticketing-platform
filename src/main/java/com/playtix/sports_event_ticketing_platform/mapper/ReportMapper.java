package com.playtix.sports_event_ticketing_platform.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.playtix.sports_event_ticketing_platform.domain.dto.report.AdminReportDto;
import com.playtix.sports_event_ticketing_platform.domain.dto.report.UserReportDto;
import com.playtix.sports_event_ticketing_platform.domain.entity.report.Report;

@Mapper(
    componentModel = "spring",
    uses = {
        TicketMapper.class,
        UserMapper.class,
        SupportMapper.class
    }
)
public interface ReportMapper {

    @Mapping(target = "ticketSummaryDto", source = "ticket")
    UserReportDto toUserReportDto(Report report);

    List<UserReportDto> toUserReportDtoList(List<Report> reports);

    @Mapping(target = "ticketSummaryDto", source = "ticket")
    @Mapping(target = "userReferenceDto", source = "user")
    @Mapping(target = "supportReferenceDto", source = "support")
    AdminReportDto toAdminReportDto(Report report);

    List<AdminReportDto> toAdminReportDtoList(List<Report> reports);
    
}
