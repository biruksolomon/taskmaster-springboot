package com.taskmaster_springboot.dto.response;

import com.taskmaster_springboot.model.enums.TaskPriority;
import com.taskmaster_springboot.model.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponseDTO {
    private UUID id;
    private UUID projectId;
    private String key;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private UUID assigneeId;
    private String assigneeName;
    private UUID reporterId;
    private LocalDate startDate;
    private LocalDate dueDate;
    private Integer estimateMinutes;
    private Integer timeSpentMinutes;
    private List<String> tags;
    private Integer commentCount;
    private Instant createdAt;
    private Instant updatedAt;
    private Boolean archived;
}
