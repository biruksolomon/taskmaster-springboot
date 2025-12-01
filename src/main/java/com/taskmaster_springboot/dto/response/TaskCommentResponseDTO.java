package com.taskmaster_springboot.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskCommentResponseDTO {
    private UUID id;
    private UUID taskId;
    private UUID authorId;
    private String authorName;
    private String content;
    private UUID parentCommentId;
    private Instant createdAt;
    private Instant editedAt;
}
