package com.taskmaster_springboot.controller;

import com.taskmaster_springboot.dto.request.UpdateUserRoleRequestDTO;
import com.taskmaster_springboot.dto.request.UpdateUserStatusRequestDTO;
import com.taskmaster_springboot.dto.response.AdminUserResponseDTO;
import com.taskmaster_springboot.dto.response.ApiResponseDTO;
import com.taskmaster_springboot.service.AdminUserService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/users")
@AllArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "User Management (Admin Only)", description = "Admin endpoints for managing users, roles, and account status")
public class AdminUserController {

    private AdminUserService adminUserService;

    /**
     * GET /api/v1/admin/users
     * Get all users (admin only)
     */
    @GetMapping
    @Operation(summary = "Get all users",
            description = "Retrieve a list of all users in the system. Admin role required.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ApiResponseDTO<List<AdminUserResponseDTO>>> getAllUsers() {
        log.info("Admin request to get all users");

        List<AdminUserResponseDTO> users = adminUserService.getAllUsers();

        return ResponseEntity.ok()
                .body(ApiResponseDTO.<List<AdminUserResponseDTO>>builder()
                        .success(true)
                        .statusCode(200)
                        .message("Users retrieved successfully")
                        .data(users)
                        .build());
    }

    /**
     * GET /api/v1/admin/users/{id}
     * Get user details by ID (admin only)
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get user details",
            description = "Retrieve detailed information for a specific user. Admin role required.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User details retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ApiResponseDTO<AdminUserResponseDTO>> getUserById(
            @Parameter(description = "User ID (UUID)", required = true)
            @PathVariable UUID id) {
        log.info("Admin request to get user details for userId: {}", id);

        AdminUserResponseDTO user = adminUserService.getUserById(id);

        return ResponseEntity.ok()
                .body(ApiResponseDTO.<AdminUserResponseDTO>builder()
                        .success(true)
                        .statusCode(200)
                        .message("User details retrieved successfully")
                        .data(user)
                        .build());
    }

    /**
     * PATCH /api/v1/admin/users/{id}/role
     * Update user role (admin only)
     */
    @PatchMapping("/{id}/role")
    @Operation(summary = "Update user role",
            description = "Change user role (e.g., USER to MANAGER, MANAGER to USER). Admin role required. " +
                    "Cannot modify ADMIN role users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User role updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid role or cannot modify ADMIN users",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "User not found or role not found",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ApiResponseDTO<AdminUserResponseDTO>> updateUserRole(
            @Parameter(description = "User ID (UUID)", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserRoleRequestDTO request) {
        log.info("Admin request to update user role for userId: {} to role: {}", id, request.getRole());

        AdminUserResponseDTO updatedUser = adminUserService.updateUserRole(id, request.getRole());

        return ResponseEntity.ok()
                .body(ApiResponseDTO.<AdminUserResponseDTO>builder()
                        .success(true)
                        .statusCode(200)
                        .message("User role updated successfully")
                        .data(updatedUser)
                        .build());
    }

    /**
     * PATCH /api/v1/admin/users/{id}/activate
     * Activate or deactivate user (admin only)
     */
    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activate/deactivate user",
            description = "Change user account status (ACTIVE, DISABLED, PENDING, BANNED). Admin role required. " +
                    "Cannot disable ADMIN role users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User status updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid status or cannot disable ADMIN users",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ApiResponseDTO<AdminUserResponseDTO>> updateUserStatus(
            @Parameter(description = "User ID (UUID)", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserStatusRequestDTO request) {
        log.info("Admin request to update user status for userId: {} to status: {}", id, request.getStatus());

        AdminUserResponseDTO updatedUser = adminUserService.updateUserStatus(id, request.getStatus());

        return ResponseEntity.ok()
                .body(ApiResponseDTO.<AdminUserResponseDTO>builder()
                        .success(true)
                        .statusCode(200)
                        .message("User status updated successfully")
                        .data(updatedUser)
                        .build());
    }

    /**
     * DELETE /api/v1/admin/users/{id}
     * Delete user (admin only)
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user",
            description = "Permanently delete a user account. Admin role required. Cannot delete ADMIN role users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Cannot delete ADMIN role users",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ApiResponseDTO<String>> deleteUser(
            @Parameter(description = "User ID (UUID)", required = true)
            @PathVariable UUID id) {
        log.info("Admin request to delete user with userId: {}", id);

        adminUserService.deleteUser(id);

        return ResponseEntity.ok()
                .body(ApiResponseDTO.<String>builder()
                        .success(true)
                        .statusCode(200)
                        .message("User deleted successfully")
                        .data("User with ID " + id + " has been deleted")
                        .build());
    }
}
