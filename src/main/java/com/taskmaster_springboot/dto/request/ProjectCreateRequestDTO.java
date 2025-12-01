package com.taskmaster_springboot.dto.request;

import com.taskmaster_springboot.model.enums.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectCreateRequestDTO {
    @NotBlank(message = "Project key is required")
    @Size(min = 1, max = 32, message = "Project key must be between 1 and 32 characters")
    private String key;

    @NotBlank(message = "Project name is required")
    @Size(min = 1, max = 255, message = "Project name must be between 1 and 255 characters")
    private String name;

    private String description;

    private UUID teamId;

    private LocalDate startDate;

    private LocalDate dueDate;

    private ProjectVisibility visibility;
}
