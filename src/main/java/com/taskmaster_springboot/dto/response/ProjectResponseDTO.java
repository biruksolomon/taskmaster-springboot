package com.taskmaster_springboot.dto.response;

import com.taskmaster_springboot.model.enums.ProjectStatus;
import com.taskmaster_springboot.model.enums.ProjectVisibility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponseDTO {
    private UUID id;
    private String key;
    private String name;
    private String description;
    private ProjectStatus status;
    private ProjectVisibility visibility;
    private UUID ownerId;
    private UUID teamId;
    private LocalDate startDate;
    private LocalDate dueDate;
    private Integer taskCount;
    private Integer memberCount;
    private Instant createdAt;
    private Instant updatedAt;
    private Boolean archived;
}
