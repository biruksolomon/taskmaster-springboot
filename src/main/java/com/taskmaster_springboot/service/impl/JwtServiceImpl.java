package com.taskmaster_springboot.service.impl;

import com.taskmaster_springboot.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@Slf4j
public class JwtServiceImpl implements JwtService {

    // ===========================================================
    //                   JWT CONFIG PROPERTIES
    // ===========================================================
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    private static final String USER_ID_CLAIM = "userId";


    // ===========================================================
    //                   PUBLIC API METHODS
    // ===========================================================

    @Override
    public String generateToken(String username) {
        return createToken(new HashMap<>(), username, jwtExpiration);
    }

    @Override
    public String generateToken(String username, Long userId) {
        Map<String, Object> claims = new HashMap<>();
        if (userId != null) claims.put(USER_ID_CLAIM, userId);
        return createToken(claims, username, jwtExpiration);
    }

    @Override
    public String generateRefreshToken(String username) {
        return createToken(new HashMap<>(), username, refreshExpiration);
    }

    @Override
    public String generateRefreshToken(String username, Long userId) {
        Map<String, Object> claims = new HashMap<>();
        if (userId != null) claims.put(USER_ID_CLAIM, userId);
        return createToken(claims, username, refreshExpiration);
    }

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public Long extractUserId(String token) {
        try {
            Claims claims = extractAllClaims(token);

            if (claims == null || claims.get(USER_ID_CLAIM) == null) {
                return null;
            }

            Object raw = claims.get(USER_ID_CLAIM);

            if (raw instanceof Number) return ((Number) raw).longValue();
            if (raw instanceof String) return Long.parseLong((String) raw);

            return null;
        } catch (Exception e) {
            log.error("❌ Failed to extract userId: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public Boolean isTokenValid(String token, UserDetails userDetails) {
        if (token == null || token.isEmpty()) return false;

        String username = extractUsername(token);
        if (username == null) return false;

        if (userDetails != null && !username.equals(userDetails.getUsername())) {
            return false;
        }

        return !isTokenExpired(token);
    }

    @Override
    public Boolean isTokenExpired(String token) {
        Date exp = extractClaim(token, Claims::getExpiration);
        return exp == null || exp.before(new Date());
    }

    @Override
    public Boolean revokeToken(String token) {
        log.warn("⚠️ revokeToken() is a no-op (no Redis blacklisting enabled).");
        return true;
    }

    @Override
    public Long getExpirationTime() {
        return jwtExpiration;
    }


    // ===========================================================
    //               TOKEN CREATION & CLAIM EXTRACTION
    // ===========================================================

    private String createToken(Map<String, Object> claims, String subject, long expirationMs) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.error("❌ Unable to parse token: {}", e.getMessage());
            return null;
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = extractAllClaims(token);
        return claims == null ? null : resolver.apply(claims);
    }


    // ===========================================================
    //                       SIGNING KEY
    // ===========================================================

    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);

        // Must be at least 256 bits (32 bytes) for HS256
        if (keyBytes.length < 32) {
            log.warn("⚠️ JWT secret too short! Generating secure fallback key.");
            return Keys.secretKeyFor(SignatureAlgorithm.HS256);
        }

        return Keys.hmacShaKeyFor(keyBytes);
    }
}
