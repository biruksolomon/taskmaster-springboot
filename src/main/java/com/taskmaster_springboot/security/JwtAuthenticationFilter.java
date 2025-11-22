package com.taskmaster_springboot.security;


import com.taskmaster_springboot.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomDetailService userDetailsService;

    // Public endpoints that don't require authentication
    private static final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestPath = request.getRequestURI();
        String method = request.getMethod();

        log.debug("🔍 JWT Filter processing: {} {}", method, requestPath);

        // 1. Skip OPTIONS (CORS Pre-flight)
        if ("OPTIONS".equalsIgnoreCase(method)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Skip public endpoints
        if (isPublicEndpoint(requestPath)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extract Authorization header
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7).trim();
        if (token.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        // 4. Extract username
        String username;
        try {
            username = jwtService.extractUsername(token);
        } catch (Exception e) {
            log.warn("❌ Invalid JWT token: {}", e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        if (username == null || username.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        // 5. Check if already authenticated
        if (SecurityContextHolder.getContext().getAuthentication() == null) {

            // Load user
            UserDetails userDetails;
            try {
                userDetails = userDetailsService.loadUserByUsername(username);
            } catch (Exception e) {
                log.warn("❌ Cannot load user: {}", username);
                filterChain.doFilter(request, response);
                return;
            }

            // Validate token
            boolean isValid;
            try {
                isValid = jwtService.isTokenValid(token, userDetails);
            } catch (Exception e) {
                log.warn("❌ Token validation failed: {}", e.getMessage());
                filterChain.doFilter(request, response);
                return;
            }

            if (isValid) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);

                log.debug("✅ Authentication successful for user: {}", username);
            } else {
                log.warn("❌ Invalid JWT token for user: {}", username);
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Check if the given path is public
     */
    private boolean isPublicEndpoint(String path) {
        if (path == null) return false;
        String p = path.toLowerCase();

        return PUBLIC_ENDPOINTS.stream().anyMatch(p::startsWith);
    }
}
