package com.taskmaster_springboot.controller;

import com.taskmaster_springboot.dto.response.ActivityLogResponseDTO;
import com.taskmaster_springboot.dto.response.ApiResponseDTO;
import com.taskmaster_springboot.service.ActivityLogService;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/activity-logs")
@AllArgsConstructor
@Slf4j
@Tag(name = "Activity Logs", description = "View activity logs (Admin only)")
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    @GetMapping("/entity/{entityId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get activity logs for an entity (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Activity logs retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ApiResponseDTO<List<ActivityLogResponseDTO>>> getEntityActivityLog(
            @Parameter(description = "Entity ID (UUID)", required = true)
            @PathVariable UUID entityId) {
        log.info("Admin retrieving activity logs for entity: {}", entityId);
        List<ActivityLogResponseDTO> logs = activityLogService.getEntityActivityLog(entityId);
        return ResponseEntity.ok()
                .body(ApiResponseDTO.<List<ActivityLogResponseDTO>>builder()
                        .success(true)
                        .statusCode(200)
                        .message("Activity logs retrieved successfully")
                        .data(logs)
                        .build());
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get activity logs by user (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User activity logs retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ApiResponseDTO<List<ActivityLogResponseDTO>>> getUserActivityLog(
            @Parameter(description = "User ID (UUID)", required = true)
            @PathVariable UUID userId) {
        log.info("Admin retrieving activity logs for user: {}", userId);
        List<ActivityLogResponseDTO> logs = activityLogService.getUserActivityLog(userId);
        return ResponseEntity.ok()
                .body(ApiResponseDTO.<List<ActivityLogResponseDTO>>builder()
                        .success(true)
                        .statusCode(200)
                        .message("User activity logs retrieved successfully")
                        .data(logs)
                        .build());
    }

    @GetMapping("/project/{projectId}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Get project activity logs (Manager/Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project activity logs retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - Manager/Admin role required",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ApiResponseDTO<List<ActivityLogResponseDTO>>> getProjectActivityLog(
            @Parameter(description = "Project ID (UUID)", required = true)
            @PathVariable UUID projectId) {
        log.info("User retrieving activity logs for project: {}", projectId);
        List<ActivityLogResponseDTO> logs = activityLogService.getProjectActivityLog(projectId);
        return ResponseEntity.ok()
                .body(ApiResponseDTO.<List<ActivityLogResponseDTO>>builder()
                        .success(true)
                        .statusCode(200)
                        .message("Project activity logs retrieved successfully")
                        .data(logs)
                        .build());
    }
}
