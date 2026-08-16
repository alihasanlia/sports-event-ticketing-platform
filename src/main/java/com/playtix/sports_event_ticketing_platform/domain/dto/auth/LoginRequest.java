package com.playtix.sports_event_ticketing_platform.domain.dto.auth;

import com.playtix.sports_event_ticketing_platform.domain.entity.members.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    private String email;
    private String password;
    private Role role;
}