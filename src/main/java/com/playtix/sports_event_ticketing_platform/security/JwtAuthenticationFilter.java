package com.playtix.sports_event_ticketing_platform.security;

import com.playtix.sports_event_ticketing_platform.domain.entity.members.BaseUser;
import com.playtix.sports_event_ticketing_platform.domain.entity.members.Role;
import com.playtix.sports_event_ticketing_platform.repository.SupportRepository;
import com.playtix.sports_event_ticketing_platform.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final SupportRepository supportRepository;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserRepository userRepository, SupportRepository supportRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.supportRepository = supportRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;
        final String userRole;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        userEmail = jwtUtil.extractUsername(jwt);
        userRole = jwtUtil.extractRole(jwt);

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            BaseUser baseUser = null;
            
            if (Role.USER.name().equals(userRole)) {
                baseUser = userRepository.findByEmail(userEmail).orElse(null);
            } else if (Role.SUPPORT.name().equals(userRole)) {
                baseUser = supportRepository.findByEmail(userEmail).orElse(null);
            }

            if (baseUser != null) {
                CustomUserDetails userDetails = new CustomUserDetails(baseUser);
                
                if (jwtUtil.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}