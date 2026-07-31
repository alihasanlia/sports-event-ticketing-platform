package com.playtix.sports_event_ticketing_platform.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.playtix.sports_event_ticketing_platform.domain.dto.user.SupportProfileDto;
import com.playtix.sports_event_ticketing_platform.domain.dto.user.SupportReferenceDto;
import com.playtix.sports_event_ticketing_platform.domain.dto.user.UpdateProfileDto;
import com.playtix.sports_event_ticketing_platform.domain.entity.members.Support;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface SupportMapper {

    SupportReferenceDto toReferenceDto(Support support);

    List<SupportReferenceDto> toReferenceDtoList(List<Support> supports);

    @Mapping(target = "profileImageUrl", ignore = true)
    SupportProfileDto toProfileDto(Support support);

    List<SupportProfileDto> toProfileDtoList(List<Support> supports);

    void updateEntityFromProfileDto(UpdateProfileDto dto, @MappingTarget Support support);

}
