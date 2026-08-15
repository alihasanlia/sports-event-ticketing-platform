package com.playtix.sports_event_ticketing_platform.security;

import com.playtix.sports_event_ticketing_platform.domain.entity.members.BaseUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final BaseUser baseUser;

    public CustomUserDetails(BaseUser baseUser) {
        this.baseUser = baseUser;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + baseUser.getRole().name()));
    }

    @Override
    public String getPassword() {
        return baseUser.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return baseUser.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public BaseUser getBaseUser() {
        return baseUser;
    }
}