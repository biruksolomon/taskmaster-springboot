package com.taskmaster_springboot.controller;

import com.taskmaster_springboot.dto.response.ApiResponseDTO;
import com.taskmaster_springboot.dto.response.NotificationResponseDTO;
import com.taskmaster_springboot.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@AllArgsConstructor
@Slf4j
@Tag(name = "Notifications", description = "Manage notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Get user notifications")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class)))
    })
    public ResponseEntity<ApiResponseDTO<List<NotificationResponseDTO>>> getUserNotifications(
            Authentication authentication) {
        log.info("Retrieving notifications for user: {}", authentication.getName());
        List<NotificationResponseDTO> notifications = notificationService.getUserNotifications(authentication.getName());
        return ResponseEntity.ok()
                .body(ApiResponseDTO.<List<NotificationResponseDTO>>builder()
                        .success(true)
                        .statusCode(200)
                        .message("Notifications retrieved successfully")
                        .data(notifications)
                        .build());
    }

    @PutMapping("/{notificationId}/read")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Mark notification as read")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification marked as read",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Notification not found",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ApiResponseDTO<String>> markAsRead(
            @Parameter(description = "Notification ID (UUID)", required = true)
            @PathVariable UUID notificationId) {
        log.info("Marking notification as read: {}", notificationId);
        notificationService.markNotificationAsRead(notificationId);
        return ResponseEntity.ok()
                .body(ApiResponseDTO.<String>builder()
                        .success(true)
                        .statusCode(200)
                        .message("Notification marked as read")
                        .data("Notification with ID " + notificationId + " marked as read")
                        .build());
    }

    @PutMapping("/read-all")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Mark all notifications as read")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All notifications marked as read",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class)))
    })
    public ResponseEntity<ApiResponseDTO<String>> markAllAsRead(Authentication authentication) {
        log.info("Marking all notifications as read for user: {}", authentication.getName());
        notificationService.markAllNotificationsAsRead(authentication.getName());
        return ResponseEntity.ok()
                .body(ApiResponseDTO.<String>builder()
                        .success(true)
                        .statusCode(200)
                        .message("All notifications marked as read")
                        .data("All notifications for user " + authentication.getName() + " have been marked as read")
                        .build());
    }

    @DeleteMapping("/{notificationId}")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Delete notification")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification deleted successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Notification not found",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ApiResponseDTO<String>> deleteNotification(
            @Parameter(description = "Notification ID (UUID)", required = true)
            @PathVariable UUID notificationId) {
        log.info("Deleting notification: {}", notificationId);
        notificationService.deleteNotification(notificationId);
        return ResponseEntity.ok()
                .body(ApiResponseDTO.<String>builder()
                        .success(true)
                        .statusCode(200)
                        .message("Notification deleted successfully")
                        .data("Notification with ID " + notificationId + " has been deleted")
                        .build());
    }
}
