package com.taskmaster_springboot.dto.request;

import com.taskmaster_springboot.model.enums.TaskPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskCreateRequestDTO {
    @NotBlank(message = "Task title is required")
    @Size(min = 1, max = 500, message = "Task title must be between 1 and 500 characters")
    private String title;

    private String description;

    private UUID projectId;

    private UUID assigneeId;

    private TaskPriority priority;

    private LocalDate startDate;

    private LocalDate dueDate;

    private Integer estimateMinutes;

    private List<String> tags;

    private UUID parentTaskId;
}
