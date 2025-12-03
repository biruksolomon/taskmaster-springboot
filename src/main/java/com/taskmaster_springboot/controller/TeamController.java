package com.taskmaster_springboot.controller;

import com.taskmaster_springboot.dto.request.TeamCreateRequestDTO;
import com.taskmaster_springboot.dto.response.ApiResponseDTO;
import com.taskmaster_springboot.dto.response.TeamResponseDTO;
import com.taskmaster_springboot.service.TeamService;
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
@RequestMapping("/api/v1/teams")
@AllArgsConstructor
@Slf4j
@Tag(name = "Team Management", description = "Manage teams and team members")
public class TeamController {

    private final TeamService teamService;

    @PostMapping
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Create a new team (Manager/Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Team created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid team data",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Forbidden - Manager role required",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ApiResponseDTO<TeamResponseDTO>> createTeam(
            @Valid @RequestBody TeamCreateRequestDTO request,
            Authentication authentication) {
        log.info("User {} creating team", authentication.getName());
        TeamResponseDTO team = teamService.createTeam(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.<TeamResponseDTO>builder()
                        .success(true)
                        .statusCode(201)
                        .message("Team created successfully")
                        .data(team)
                        .build());
    }

    @GetMapping("/{teamId}")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Get team details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Team retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Team not found",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ApiResponseDTO<TeamResponseDTO>> getTeam(
            @Parameter(description = "Team ID (UUID)", required = true)
            @PathVariable UUID teamId) {
        log.info("Retrieving team: {}", teamId);
        TeamResponseDTO team = teamService.getTeamById(teamId);
        return ResponseEntity.ok()
                .body(ApiResponseDTO.<TeamResponseDTO>builder()
                        .success(true)
                        .statusCode(200)
                        .message("Team retrieved successfully")
                        .data(team)
                        .build());
    }

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Get user's teams")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Teams retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class)))
    })
    public ResponseEntity<ApiResponseDTO<List<TeamResponseDTO>>> getUserTeams(Authentication authentication) {
        log.info("Retrieving teams for user: {}", authentication.getName());
        List<TeamResponseDTO> teams = teamService.getUserTeams(authentication.getName());
        return ResponseEntity.ok()
                .body(ApiResponseDTO.<List<TeamResponseDTO>>builder()
                        .success(true)
                        .statusCode(200)
                        .message("Teams retrieved successfully")
                        .data(teams)
                        .build());
    }

    @PutMapping("/{teamId}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Update team (Manager/Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Team updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Team not found",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Forbidden - Manager role required",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ApiResponseDTO<TeamResponseDTO>> updateTeam(
            @Parameter(description = "Team ID (UUID)", required = true)
            @PathVariable UUID teamId,
            @Valid @RequestBody TeamCreateRequestDTO request,
            Authentication authentication) {
        log.info("User {} updating team: {}", authentication.getName(), teamId);
        TeamResponseDTO team = teamService.updateTeam(teamId, request, authentication.getName());
        return ResponseEntity.ok()
                .body(ApiResponseDTO.<TeamResponseDTO>builder()
                        .success(true)
                        .statusCode(200)
                        .message("Team updated successfully")
                        .data(team)
                        .build());
    }

    @DeleteMapping("/{teamId}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Delete team (Manager/Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Team deleted successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Team not found",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Forbidden - Manager role required",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ApiResponseDTO<String>> deleteTeam(
            @Parameter(description = "Team ID (UUID)", required = true)
            @PathVariable UUID teamId,
            Authentication authentication) {
        log.info("User {} deleting team: {}", authentication.getName(), teamId);
        teamService.deleteTeam(teamId, authentication.getName());
        return ResponseEntity.ok()
                .body(ApiResponseDTO.<String>builder()
                        .success(true)
                        .statusCode(200)
                        .message("Team deleted successfully")
                        .data("Team with ID " + teamId + " has been deleted")
                        .build());
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all teams (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All teams retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ApiResponseDTO<List<TeamResponseDTO>>> getAllTeams() {
        log.info("Admin retrieving all teams");
        List<TeamResponseDTO> teams = teamService.getAllTeams();
        return ResponseEntity.ok()
                .body(ApiResponseDTO.<List<TeamResponseDTO>>builder()
                        .success(true)
                        .statusCode(200)
                        .message("All teams retrieved successfully")
                        .data(teams)
                        .build());
    }
}
