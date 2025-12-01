package com.taskmaster_springboot.service;

import com.taskmaster_springboot.dto.response.ActivityLogResponseDTO;

import java.util.List;
import java.util.UUID;

public interface ActivityLogService {
    void logActivity(UUID actorId, String action, String entityType, UUID entityId, String data);
    List<ActivityLogResponseDTO> getEntityActivityLog(UUID entityId);
    List<ActivityLogResponseDTO> getUserActivityLog(UUID userId);
    List<ActivityLogResponseDTO> getProjectActivityLog(UUID projectId);
}
