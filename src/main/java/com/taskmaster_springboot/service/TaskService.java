package com.taskmaster_springboot.service;

import com.taskmaster_springboot.dto.request.TaskCreateRequestDTO;
import com.taskmaster_springboot.dto.response.TaskResponseDTO;

import java.util.List;
import java.util.UUID;

public interface TaskService {
    TaskResponseDTO createTask(TaskCreateRequestDTO request, String email);
    TaskResponseDTO getTaskById(UUID taskId);
    List<TaskResponseDTO> getProjectTasks(UUID projectId);
    List<TaskResponseDTO> getUserAssignedTasks(String email);
    TaskResponseDTO updateTask(UUID taskId, TaskCreateRequestDTO request, String email);
    void deleteTask(UUID taskId, String email);
    void assignTask(UUID taskId, UUID assigneeId, String email);
    void updateTaskStatus(UUID taskId, String status, String email);
    List<TaskResponseDTO> filterTasks(UUID projectId, String status, String priority, String assigneeId);
}
