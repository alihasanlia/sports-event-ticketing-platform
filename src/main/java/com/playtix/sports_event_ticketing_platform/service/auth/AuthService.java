package com.playtix.sports_event_ticketing_platform.service.auth;

import com.playtix.sports_event_ticketing_platform.domain.entity.members.BaseUser;
import com.playtix.sports_event_ticketing_platform.domain.entity.members.Role;
import com.playtix.sports_event_ticketing_platform.domain.dto.auth.AuthResponse;
import com.playtix.sports_event_ticketing_platform.domain.dto.auth.LoginRequest;
import com.playtix.sports_event_ticketing_platform.repository.SupportRepository;
import com.playtix.sports_event_ticketing_platform.repository.UserRepository;
import com.playtix.sports_event_ticketing_platform.security.CustomUserDetails;
import com.playtix.sports_event_ticketing_platform.security.JwtUtil;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final SupportRepository supportRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, SupportRepository supportRepository,
                       PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.supportRepository = supportRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse login(LoginRequest request) {
        BaseUser user = null;

        if (request.getRole() == Role.USER) {
            user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("The username or password is incorrect!"));
        } else if (request.getRole() == Role.SUPPORT) {
            user = supportRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("The username or password is incorrect!"));
        } else {
            throw new IllegalArgumentException("The username or password is incorrect!");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("The username or password is incorrect!");
        }

        CustomUserDetails userDetails = new CustomUserDetails(user);
        String jwtToken = jwtUtil.generateToken(userDetails, user.getRole().name());

        return new AuthResponse(jwtToken);
    }
}