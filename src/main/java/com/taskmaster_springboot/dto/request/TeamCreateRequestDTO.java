package com.taskmaster_springboot.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamCreateRequestDTO {
    @NotBlank(message = "Team name is required")
    @Size(min = 2, max = 200, message = "Team name must be between 2 and 200 characters")
    private String name;

    @Size(max = 200, message = "Slug must be less than 200 characters")
    private String slug;

    private String description;
}
