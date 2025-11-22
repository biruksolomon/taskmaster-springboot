package com.taskmaster_springboot.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String generateToken(String username);
    String generateToken(String username, Long userId);// New
    String generateRefreshToken(String username);
    String generateRefreshToken(String username, Long userId);//New
    String extractUsername(String token);
    Long extractUserId(String token);// New
    Boolean isTokenValid(String token, UserDetails userDetails);
    Boolean isTokenExpired(String token);
    Boolean revokeToken(String token);
    Long getExpirationTime();
}
