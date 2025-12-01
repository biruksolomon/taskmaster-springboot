package com.taskmaster_springboot.service.impl;

import com.taskmaster_springboot.dto.response.ActivityLogResponseDTO;
import com.taskmaster_springboot.exceptions.ResourceNotFoundException;
import com.taskmaster_springboot.model.ActivityLogs;
import com.taskmaster_springboot.model.Users;
import com.taskmaster_springboot.repository.ActivityLogsRepository;
import com.taskmaster_springboot.repository.UsersRepository;
import com.taskmaster_springboot.service.ActivityLogService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class ActivityLogServiceImpl implements ActivityLogService {

    private final ActivityLogsRepository activityLogsRepository;
    private final UsersRepository usersRepository;

    @Override
    public void logActivity(UUID actorId, String action, String entityType, UUID entityId, String data) {
        Users actor = usersRepository.findById(actorId)
                .orElseThrow(() -> new ResourceNotFoundException("Actor not found"));

        ActivityLogs logs = ActivityLogs.builder()
                .actor(actor)
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .data(data)
                .createdAt(Instant.now())
                .build();

        activityLogsRepository.save(logs);
        log.info("Activity logged: {} - {}", action, entityType);
    }

    @Override
    public List<ActivityLogResponseDTO> getEntityActivityLog(UUID entityId) {
        return activityLogsRepository.findByEntityId(entityId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ActivityLogResponseDTO> getUserActivityLog(UUID userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return activityLogsRepository.findByActor(user).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ActivityLogResponseDTO> getProjectActivityLog(UUID projectId) {
        return activityLogsRepository.findByEntityId(projectId).stream()
                .filter(log -> "PROJECT".equals(log.getEntityType()))
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private ActivityLogResponseDTO mapToDTO(ActivityLogs log) {
        return ActivityLogResponseDTO.builder()
                .id(log.getId())
                .actorId(log.getActor().getUserId())
                .actorName(log.getActor().getFirstName() + " " + log.getActor().getLastName())
                .action(log.getAction())
                .entityType(log.getEntityType())
                .entityId(log.getEntityId())
                .data(log.getData())
                .createdAt(log.getCreatedAt())
                .build();
    }
}
