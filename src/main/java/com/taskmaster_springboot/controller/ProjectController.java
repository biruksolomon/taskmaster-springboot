package com.taskmaster_springboot.controller;

import com.taskmaster_springboot.dto.request.ProjectCreateRequestDTO;
import com.taskmaster_springboot.dto.response.ApiResponseDTO;
import com.taskmaster_springboot.dto.response.ProjectResponseDTO;
import com.taskmaster_springboot.service.ProjectService;
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
@RequestMapping("/api/v1/projects")
@AllArgsConstructor
@Slf4j
@Tag(name = "Project Management", description = "Manage projects")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Create a new project (Manager/Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Project created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid project data",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Forbidden - Manager role required",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ApiResponseDTO<ProjectResponseDTO>> createProject(
            @Valid @RequestBody ProjectCreateRequestDTO request,
            Authentication authentication) {
        log.info("User {} creating project", authentication.getName());
        ProjectResponseDTO project = projectService.createProject(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.<ProjectResponseDTO>builder()
                        .success(true)
                        .statusCode(201)
                        .message("Project created successfully")
                        .data(project)
                        .build());
    }

    @GetMapping("/{projectId}")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Get project details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Project not found",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ApiResponseDTO<ProjectResponseDTO>> getProject(
            @Parameter(description = "Project ID (UUID)", required = true)
            @PathVariable UUID projectId) {
        log.info("Retrieving project: {}", projectId);
        ProjectResponseDTO project = projectService.getProjectById(projectId);
        return ResponseEntity.ok()
                .body(ApiResponseDTO.<ProjectResponseDTO>builder()
                        .success(true)
                        .statusCode(200)
                        .message("Project retrieved successfully")
                        .data(project)
                        .build());
    }

    @GetMapping("/team/{teamId}")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Get team projects")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Team projects retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class)))
    })
    public ResponseEntity<ApiResponseDTO<List<ProjectResponseDTO>>> getTeamProjects(
            @Parameter(description = "Team ID (UUID)", required = true)
            @PathVariable UUID teamId) {
        log.info("Retrieving projects for team: {}", teamId);
        List<ProjectResponseDTO> projects = projectService.getTeamProjects(teamId);
        return ResponseEntity.ok()
                .body(ApiResponseDTO.<List<ProjectResponseDTO>>builder()
                        .success(true)
                        .statusCode(200)
                        .message("Team projects retrieved successfully")
                        .data(projects)
                        .build());
    }

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Get user's projects")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User projects retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class)))
    })
    public ResponseEntity<ApiResponseDTO<List<ProjectResponseDTO>>> getUserProjects(Authentication authentication) {
        log.info("Retrieving projects for user: {}", authentication.getName());
        List<ProjectResponseDTO> projects = projectService.getUserProjects(authentication.getName());
        return ResponseEntity.ok()
                .body(ApiResponseDTO.<List<ProjectResponseDTO>>builder()
                        .success(true)
                        .statusCode(200)
                        .message("User projects retrieved successfully")
                        .data(projects)
                        .build());
    }

    @PutMapping("/{projectId}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Update project (Manager/Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Project not found",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Forbidden - Manager role required",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ApiResponseDTO<ProjectResponseDTO>> updateProject(
            @Parameter(description = "Project ID (UUID)", required = true)
            @PathVariable UUID projectId,
            @Valid @RequestBody ProjectCreateRequestDTO request,
            Authentication authentication) {
        log.info("User {} updating project: {}", authentication.getName(), projectId);
        ProjectResponseDTO project = projectService.updateProject(projectId, request, authentication.getName());
        return ResponseEntity.ok()
                .body(ApiResponseDTO.<ProjectResponseDTO>builder()
                        .success(true)
                        .statusCode(200)
                        .message("Project updated successfully")
                        .data(project)
                        .build());
    }

    @DeleteMapping("/{projectId}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Delete project (Manager/Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project deleted successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Project not found",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Forbidden - Manager role required",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ApiResponseDTO<String>> deleteProject(
            @Parameter(description = "Project ID (UUID)", required = true)
            @PathVariable UUID projectId,
            Authentication authentication) {
        log.info("User {} deleting project: {}", authentication.getName(), projectId);
        projectService.deleteProject(projectId, authentication.getName());
        return ResponseEntity.ok()
                .body(ApiResponseDTO.<String>builder()
                        .success(true)
                        .statusCode(200)
                        .message("Project deleted successfully")
                        .data("Project with ID " + projectId + " has been deleted")
                        .build());
    }
}
