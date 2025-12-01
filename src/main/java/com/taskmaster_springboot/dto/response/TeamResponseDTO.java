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
public class TeamResponseDTO {
    private UUID id;
    private String name;
    private String slug;
    private String description;
    private UUID createdById;
    private Integer memberCount;
    private Instant createdAt;
    private Instant updatedAt;
    private Boolean archived;
}
