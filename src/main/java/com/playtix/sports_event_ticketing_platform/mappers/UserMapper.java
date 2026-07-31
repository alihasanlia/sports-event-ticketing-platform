package com.playtix.sports_event_ticketing_platform.mappers;

import com.playtix.sports_event_ticketing_platform.domain.dto.user.UpdateProfileDto;
import com.playtix.sports_event_ticketing_platform.domain.dto.user.UserProfileDto;
import com.playtix.sports_event_ticketing_platform.domain.dto.user.UserReferenceDto;
import com.playtix.sports_event_ticketing_platform.domain.entity.members.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserMapper {

    UserReferenceDto toReferenceDto(User user);
    
    List<UserReferenceDto> toReferenceDtoList(List<User> users);
    
    @Mappings({
        @Mapping(target = "profileImageUrl", ignore = true),
    })
    UserProfileDto toProfileDto(User user);
    
    List<UserProfileDto> toProfileDtoList(List<User> users);
    
    void updateEntityFromProfileDto(UpdateProfileDto dto, @MappingTarget User user);
}