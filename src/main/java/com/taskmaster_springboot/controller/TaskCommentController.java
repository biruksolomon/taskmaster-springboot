package com.taskmaster_springboot.controller;

import com.taskmaster_springboot.dto.request.TaskCommentCreateRequestDTO;
import com.taskmaster_springboot.dto.response.ApiResponseDTO;
import com.taskmaster_springboot.dto.response.TaskCommentResponseDTO;
import com.taskmaster_springboot.service.TaskCommentService;
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
@RequestMapping("/api/v1/comments")
@AllArgsConstructor
@Slf4j
@Tag(name = "Task Comments", description = "Manage task comments")
public class TaskCommentController {

    private final TaskCommentService taskCommentService;

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Add comment to task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Comment added successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid comment data",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ApiResponseDTO<TaskCommentResponseDTO>> addComment(
            @Valid @RequestBody TaskCommentCreateRequestDTO request,
            Authentication authentication) {
        log.info("User {} adding comment to task {}", authentication.getName(), request.getTaskId());
        TaskCommentResponseDTO comment = taskCommentService.addComment(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.<TaskCommentResponseDTO>builder()
                        .success(true)
                        .statusCode(201)
                        .message("Comment added successfully")
                        .data(comment)
                        .build());
    }

    @GetMapping("/{commentId}")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Get comment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Comment not found",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ApiResponseDTO<TaskCommentResponseDTO>> getComment(
            @Parameter(description = "Comment ID (UUID)", required = true)
            @PathVariable UUID commentId) {
        log.info("Retrieving comment: {}", commentId);
        TaskCommentResponseDTO comment = taskCommentService.getCommentById(commentId);
        return ResponseEntity.ok()
                .body(ApiResponseDTO.<TaskCommentResponseDTO>builder()
                        .success(true)
                        .statusCode(200)
                        .message("Comment retrieved successfully")
                        .data(comment)
                        .build());
    }

    @GetMapping("/task/{taskId}")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Get task comments")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comments retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class)))
    })
    public ResponseEntity<ApiResponseDTO<List<TaskCommentResponseDTO>>> getTaskComments(
            @Parameter(description = "Task ID (UUID)", required = true)
            @PathVariable UUID taskId) {
        log.info("Retrieving comments for task: {}", taskId);
        List<TaskCommentResponseDTO> comments = taskCommentService.getTaskComments(taskId);
        return ResponseEntity.ok()
                .body(ApiResponseDTO.<List<TaskCommentResponseDTO>>builder()
                        .success(true)
                        .statusCode(200)
                        .message("Comments retrieved successfully")
                        .data(comments)
                        .build());
    }

    @PutMapping("/{commentId}")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Update comment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Comment not found",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ApiResponseDTO<TaskCommentResponseDTO>> updateComment(
            @Parameter(description = "Comment ID (UUID)", required = true)
            @PathVariable UUID commentId,
            @Parameter(description = "New comment content", required = true)
            @RequestParam String content,
            Authentication authentication) {
        log.info("User {} updating comment: {}", authentication.getName(), commentId);
        TaskCommentResponseDTO comment = taskCommentService.updateComment(commentId, content, authentication.getName());
        return ResponseEntity.ok()
                .body(ApiResponseDTO.<TaskCommentResponseDTO>builder()
                        .success(true)
                        .statusCode(200)
                        .message("Comment updated successfully")
                        .data(comment)
                        .build());
    }

    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Delete comment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment deleted successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Comment not found",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ApiResponseDTO<String>> deleteComment(
            @Parameter(description = "Comment ID (UUID)", required = true)
            @PathVariable UUID commentId,
            Authentication authentication) {
        log.info("User {} deleting comment: {}", authentication.getName(), commentId);
        taskCommentService.deleteComment(commentId, authentication.getName());
        return ResponseEntity.ok()
                .body(ApiResponseDTO.<String>builder()
                        .success(true)
                        .statusCode(200)
                        .message("Comment deleted successfully")
                        .data("Comment with ID " + commentId + " has been deleted")
                        .build());
    }
}
