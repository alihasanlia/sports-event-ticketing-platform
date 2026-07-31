package com.playtix.sports_event_ticketing_platform.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.playtix.sports_event_ticketing_platform.domain.dto.payment.AdminPaymentDto;
import com.playtix.sports_event_ticketing_platform.domain.dto.payment.UserPaymentDto;
import com.playtix.sports_event_ticketing_platform.domain.entity.payment.Payment;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface PaymentMapper {

    UserPaymentDto toUserPaymentDto(Payment payment);

    List<UserPaymentDto> toUserPaymentDtoList(List<Payment> payments);

    @Mapping(target = "userReferenceDto", source = "user")
    AdminPaymentDto toAdminPaymentDto(Payment payment);

    List<AdminPaymentDto> toAdminPaymentDtoList(List<Payment> payments);
    
}
