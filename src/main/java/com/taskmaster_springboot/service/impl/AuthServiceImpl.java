package com.taskmaster_springboot.service.impl;

import com.taskmaster_springboot.dto.response.AuthResponseDTO;
import com.taskmaster_springboot.dto.response.UserAuthResponseDTO;
import com.taskmaster_springboot.exceptions.AuthenticationException;
import com.taskmaster_springboot.exceptions.ResourceNotFoundException;
import com.taskmaster_springboot.model.Users;
import com.taskmaster_springboot.repository.UsersRepository;
import com.taskmaster_springboot.service.AuthService;
import com.taskmaster_springboot.service.EmailService;
import com.taskmaster_springboot.service.JwtService;
import com.taskmaster_springboot.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UsersRepository usersRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final UserService userService;

    /**
     * Authenticate user with email and password
     */
    @Override
    public AuthResponseDTO authenticateUser(String email, String password) {
        log.info("Authenticating user with email: {}", email);

        Users user = usersRepository.findByEmail(email);

        if (user == null) {
            log.warn("Authentication failed - user not found: {}", email);
            throw new AuthenticationException("Invalid email or password");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.warn("Authentication failed - invalid password for user: {}", email);
            throw new AuthenticationException("Invalid email or password");
        }

        if (!user.getEmailVerified()) {
            log.warn("Authentication failed - email not verified for user: {}", email);
            throw new AuthenticationException("Email not verified. Please verify your email first.");
        }

        // Update last login
        userService.updateLastLogin(email);

        // Generate JWT tokens
        String accessToken = jwtService.generateToken(user.getUsername(), user.getUserId().getLeastSignificantBits());
        String refreshToken = jwtService.generateRefreshToken(user.getUsername(), user.getUserId().getLeastSignificantBits());

        log.info("Authentication successful for user: {}", email);

        return AuthResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpirationTime() / 1000) // Convert to seconds
                .user(mapToUserAuthResponse(user))
                .build();
    }

    /**
     * Generate password reset token and send email
     */
    @Override
    public void generatePasswordResetToken(String email) {
        log.info("Generating password reset token for email: {}", email);

        Users user = usersRepository.findByEmail(email);

        if (user == null) {
            // Don't reveal if email exists for security
            log.warn("Password reset requested for non-existent email: {}", email);
            return;
        }

        // Generate secure reset token
        String resetToken = generateSecureToken();
        user.setPasswordResetToken(resetToken);
        user.setPasswordResetTokenExpireAt(Instant.now().plusSeconds(3600)); // 1 hour expiration

        usersRepository.save(user);

        // Send email with reset link
        emailService.sendPasswordResetEmail(email, resetToken);

        log.info("Password reset token sent for email: {}", email);
    }

    /**
     * Reset password with token
     */
    @Override
    public void resetPassword(String token, String newPassword) {
        log.info("Resetting password with token");

        Users user = usersRepository.findByPasswordResetToken(token);

        if (user == null) {
            log.warn("Password reset failed - invalid token");
            throw new ResourceNotFoundException("Invalid or expired reset token");
        }

        if (user.getPasswordResetTokenExpireAt() == null ||
                user.getPasswordResetTokenExpireAt().isBefore(Instant.now())) {
            log.warn("Password reset failed - token expired for user: {}", user.getEmail());
            throw new AuthenticationException("Reset token has expired. Please request a new one.");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpireAt(null);

        usersRepository.save(user);

        log.info("Password reset successful for user: {}", user.getEmail());
    }

    /**
     * Map Users entity to UserAuthResponseDTO
     */
    private UserAuthResponseDTO mapToUserAuthResponse(Users user) {
        return UserAuthResponseDTO.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .avatarUrl(user.getAvatarUrl())
                .status(user.getStatus())
                .emailVerified(user.getEmailVerified())
                .createdAt(user.getCreatedAt())
                .lastLoginAt(user.getLastLoginAt())
                .build();
    }

    /**
     * Generate secure random token
     */
    private String generateSecureToken() {
        SecureRandom random = new SecureRandom();
        byte[] tokenBytes = new byte[32];
        random.nextBytes(tokenBytes);
        return java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }
}
