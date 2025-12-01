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
public class ActivityLogResponseDTO {
    private Long id;
    private UUID actorId;
    private String actorName;
    private String action;
    private String entityType;
    private UUID entityId;
    private String data;
    private Instant createdAt;
}
