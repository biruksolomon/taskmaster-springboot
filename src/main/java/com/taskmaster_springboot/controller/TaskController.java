package com.taskmaster_springboot.controller;

import com.taskmaster_springboot.dto.request.TaskCreateRequestDTO;
import com.taskmaster_springboot.dto.response.ApiResponseDTO;
import com.taskmaster_springboot.dto.response.TaskResponseDTO;
import com.taskmaster_springboot.service.TaskService;
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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tasks")
@AllArgsConstructor
@Slf4j
@Tag(name = "Task Management", description = "Manage tasks")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Create a new task (Manager/Admin or assigned Users)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Task created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid task data",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Forbidden - Manager role required",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ApiResponseDTO<TaskResponseDTO>> createTask(
            @Valid @RequestBody TaskCreateRequestDTO request,
            Authentication authentication) {
        log.info("User {} creating task", authentication.getName());
        TaskResponseDTO task = taskService.createTask(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.<TaskResponseDTO>builder()
                        .success(true)
                        .statusCode(201)
                        .message("Task created successfully")
                        .data(task)
                        .build());
    }

    @GetMapping("/{taskId}")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Get task details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Task not found",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ApiResponseDTO<TaskResponseDTO>> getTask(
            @Parameter(description = "Task ID (UUID)", required = true)
            @PathVariable UUID taskId) {
        log.info("Retrieving task: {}", taskId);
        TaskResponseDTO task = taskService.getTaskById(taskId);
        return ResponseEntity.ok()
                .body(ApiResponseDTO.<TaskResponseDTO>builder()
                        .success(true)
                        .statusCode(200)
                        .message("Task retrieved successfully")
                        .data(task)
                        .build());
    }

    @GetMapping("/project/{projectId}")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Get project tasks")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project tasks retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class)))
    })
    public ResponseEntity<ApiResponseDTO<List<TaskResponseDTO>>> getProjectTasks(
            @Parameter(description = "Project ID (UUID)", required = true)
            @PathVariable UUID projectId) {
        log.info("Retrieving tasks for project: {}", projectId);
        List<TaskResponseDTO> tasks = taskService.getProjectTasks(projectId);
        return ResponseEntity.ok()
                .body(ApiResponseDTO.<List<TaskResponseDTO>>builder()
                        .success(true)
                        .statusCode(200)
                        .message("Project tasks retrieved successfully")
                        .data(tasks)
                        .build());
    }

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Get user's assigned tasks")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User tasks retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class)))
    })
    public ResponseEntity<ApiResponseDTO<List<TaskResponseDTO>>> getUserAssignedTasks(Authentication authentication) {
        log.info("Retrieving assigned tasks for user: {}", authentication.getName());
        List<TaskResponseDTO> tasks = taskService.getUserAssignedTasks(authentication.getName());
        return ResponseEntity.ok()
                .body(ApiResponseDTO.<List<TaskResponseDTO>>builder()
                        .success(true)
                        .statusCode(200)
                        .message("User tasks retrieved successfully")
                        .data(tasks)
                        .build());
    }

    @PutMapping("/{taskId}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Update task details (Manager/Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Task not found",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Forbidden - Manager role required",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ApiResponseDTO<TaskResponseDTO>> updateTask(
            @Parameter(description = "Task ID (UUID)", required = true)
            @PathVariable UUID taskId,
            @Valid @RequestBody TaskCreateRequestDTO request,
            Authentication authentication) {
        log.info("User {} updating task: {}", authentication.getName(), taskId);
        TaskResponseDTO task = taskService.updateTask(taskId, request, authentication.getName());
        return ResponseEntity.ok()
                .body(ApiResponseDTO.<TaskResponseDTO>builder()
                        .success(true)
                        .statusCode(200)
                        .message("Task updated successfully")
                        .data(task)
                        .build());
    }

    @DeleteMapping("/{taskId}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Delete task (Manager/Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task deleted successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Task not found",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Forbidden - Manager role required",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ApiResponseDTO<String>> deleteTask(
            @Parameter(description = "Task ID (UUID)", required = true)
            @PathVariable UUID taskId,
            Authentication authentication) {
        log.info("User {} deleting task: {}", authentication.getName(), taskId);
        taskService.deleteTask(taskId, authentication.getName());
        return ResponseEntity.ok()
                .body(ApiResponseDTO.<String>builder()
                        .success(true)
                        .statusCode(200)
                        .message("Task deleted successfully")
                        .data("Task with ID " + taskId + " has been deleted")
                        .build());
    }

    @PutMapping("/{taskId}/assign/{assigneeId}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Assign task to user (Manager/Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task assigned successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class)))
    })
    public ResponseEntity<ApiResponseDTO<String>> assignTask(
            @Parameter(description = "Task ID (UUID)", required = true)
            @PathVariable UUID taskId,
            @Parameter(description = "Assignee ID (UUID)", required = true)
            @PathVariable UUID assigneeId,
            Authentication authentication) {
        log.info("User {} assigning task {} to user {}", authentication.getName(), taskId, assigneeId);
        taskService.assignTask(taskId, assigneeId, authentication.getName());
        return ResponseEntity.ok()
                .body(ApiResponseDTO.<String>builder()
                        .success(true)
                        .statusCode(200)
                        .message("Task assigned successfully")
                        .data("Task with ID " + taskId + " has been assigned to user " + assigneeId)
                        .build());
    }

    @PutMapping("/{taskId}/status/{status}")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Update task status (Assigned users can update own tasks)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task status updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class)))
    })
    public ResponseEntity<ApiResponseDTO<String>> updateTaskStatus(
            @Parameter(description = "Task ID (UUID)", required = true)
            @PathVariable UUID taskId,
            @Parameter(description = "New task status", required = true)
            @PathVariable String status,
            Authentication authentication) {
        log.info("User {} updating task {} status to {}", authentication.getName(), taskId, status);
        taskService.updateTaskStatus(taskId, status, authentication.getName());
        return ResponseEntity.ok()
                .body(ApiResponseDTO.<String>builder()
                        .success(true)
                        .statusCode(200)
                        .message("Task status updated successfully")
                        .data("Task with ID " + taskId + " status updated to " + status)
                        .build());
    }
}
