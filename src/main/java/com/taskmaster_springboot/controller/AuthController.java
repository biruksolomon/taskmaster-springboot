package com.taskmaster_springboot.controller;

import com.taskmaster_springboot.dto.request.*;
import com.taskmaster_springboot.dto.response.ApiResponseDTO;
import com.taskmaster_springboot.dto.response.AuthResponseDTO;
import com.taskmaster_springboot.dto.response.UserCreateResponseDTO;
import com.taskmaster_springboot.service.AuthService;
import com.taskmaster_springboot.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "APIs for user authentication and password management")
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    /**
     * POST /api/v1/auth/register
     * User registration with auto-assigned USER role
     */
    @PostMapping("/register")
    @Operation(summary = "Register a new user",
            description = "Create a new user account with email and password. User role is auto-assigned as USER. " +
                    "A verification email will be sent to the provided email address.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input or email already exists",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ApiResponseDTO<UserCreateResponseDTO>> register(
            @Valid @RequestBody UserCreateRequestDTO request) {
        log.info("User registration attempt for email: {}", request.getEmail());

        UserCreateResponseDTO response = userService.createUser(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.<UserCreateResponseDTO>builder()
                        .success(true)
                        .statusCode(201)
                        .message("User registered successfully. Verification email sent.")
                        .data(response)
                        .build());
    }

    /**
     * POST /api/v1/auth/login
     * Login with email and password, returns JWT token
     */
    @PostMapping("/login")
    @Operation(summary = "User login",
            description = "Authenticate user with email and password. Returns JWT access token and refresh token on successful authentication.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid email or password",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Authentication failed",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ApiResponseDTO<AuthResponseDTO>> login(
            @Valid @RequestBody LoginRequestDTO request) {
        log.info("Login attempt for email: {}", request.getEmail());

        AuthResponseDTO response = authService.authenticateUser(request.getEmail(), request.getPassword());

        return ResponseEntity.ok()
                .body(ApiResponseDTO.<AuthResponseDTO>builder()
                        .success(true)
                        .statusCode(200)
                        .message("Login successful")
                        .data(response)
                        .build());
    }

    /**
     * GET /api/v1/auth/verify?token={token}
     * Verify user email with verification code/token
     */
    @GetMapping("/verify")
    @Operation(summary = "Verify user email",
            description = "Verify user email address with the verification token sent to their email. " +
                    "Must provide both email and token as query parameters.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email verified successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid or expired verification token",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ApiResponseDTO<String>> verifyEmail(
            @Parameter(description = "Email address to verify", required = true)
            @RequestParam String email,
            @Parameter(description = "Verification token sent to email", required = true)
            @RequestParam String token) {
        log.info("Email verification attempt for email: {}", email);

        userService.verifyEmailCode(email, token);

        return ResponseEntity.ok()
                .body(ApiResponseDTO.<String>builder()
                        .success(true)
                        .statusCode(200)
                        .message("Email verified successfully")
                        .data("Your account is now active")
                        .build());
    }

    /**
     * POST /api/v1/auth/forgot-password
     * Request password reset email
     */
    @PostMapping("/forgot-password")
    @Operation(summary = "Request password reset",
            description = "Send a password reset link to the user's email address. User must verify their email " +
                    "before requesting password reset.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset email sent successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid email or email not verified",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error or email sending failed",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ApiResponseDTO<String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequestDTO request) {
        log.info("Password reset requested for email: {}", request.getEmail());

        authService.generatePasswordResetToken(request.getEmail());

        return ResponseEntity.ok()
                .body(ApiResponseDTO.<String>builder()
                        .success(true)
                        .statusCode(200)
                        .message("Password reset email sent successfully")
                        .data("Check your email for password reset instructions")
                        .build());
    }

    /**
     * POST /api/v1/auth/reset-password
     * Reset password with token
     */
    @PostMapping("/reset-password")
    @Operation(summary = "Reset password with token",
            description = "Reset user password using the token received in the password reset email. " +
                    "Passwords must match and follow security requirements.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid token, expired token, or password mismatch",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ApiResponseDTO<String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequestDTO request) {
        log.info("Password reset attempt with token");

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        authService.resetPassword(request.getToken(), request.getNewPassword());

        return ResponseEntity.ok()
                .body(ApiResponseDTO.<String>builder()
                        .success(true)
                        .statusCode(200)
                        .message("Password reset successfully")
                        .data("You can now login with your new password")
                        .build());
    }
}
