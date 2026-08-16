package com.playtix.sports_event_ticketing_platform.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.playtix.sports_event_ticketing_platform.domain.dto.reservation.AdminReservationDto;
import com.playtix.sports_event_ticketing_platform.domain.dto.reservation.UserReservationDto;
import com.playtix.sports_event_ticketing_platform.domain.entity.reservation.Reservation;

@Mapper(
    componentModel = "spring",
    uses = {
        TicketMapper.class,
        UserMapper.class
    }
)
public interface ReservationMapper {

    @Mapping(target = "ticketDetailsDto", source = "ticket")
    @Mapping(target = "isExpired", expression = "java(reservation.isExpiredNow())")
    @Mapping(target = "timeRemaining", expression = "java(reservation.generateTimeRemainingString())")
    UserReservationDto toUserReservationDto(Reservation reservation);

    List<UserReservationDto> toUserReservationDtoList(List<Reservation> reservations);

    @Mapping(target = "ticketDetailsDto", source = "ticket")
    @Mapping(target = "isExpired", expression = "java(reservation.isExpiredNow())")
    @Mapping(target = "timeRemaining", expression = "java(reservation.generateTimeRemainingString())")
    @Mapping(target = "userReferenceDto", source = "user")
    AdminReservationDto toAdminReservationDto(Reservation reservation);

    List<AdminReservationDto> toAdminReservationDtoList(List<Reservation> reservations);
    
}
