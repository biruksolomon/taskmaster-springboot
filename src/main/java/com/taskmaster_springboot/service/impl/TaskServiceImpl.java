package com.taskmaster_springboot.service.impl;

import com.taskmaster_springboot.dto.request.TaskCreateRequestDTO;
import com.taskmaster_springboot.dto.response.TaskResponseDTO;
import com.taskmaster_springboot.exceptions.AuthenticationException;
import com.taskmaster_springboot.exceptions.ResourceNotFoundException;
import com.taskmaster_springboot.model.Projects;
import com.taskmaster_springboot.model.Tasks;
import com.taskmaster_springboot.model.Users;
import com.taskmaster_springboot.model.enums.TaskStatus;
import com.taskmaster_springboot.repository.ProjectRepository;
import com.taskmaster_springboot.repository.TaskRepository;
import com.taskmaster_springboot.repository.UsersRepository;
import com.taskmaster_springboot.service.TaskService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UsersRepository usersRepository;

    @Override
    public TaskResponseDTO createTask(TaskCreateRequestDTO request, String email) {
        log.info("Creating task: {}", request.getTitle());

        Users user = usersRepository.findByEmail(email);
        if (user == null) {
            throw new AuthenticationException("User not found");
        }

        Projects project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        Users assignee = null;
        if (request.getAssigneeId() != null) {
            assignee = usersRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Assignee not found"));
        }

        Tasks task = Tasks.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .project(project)
                .assignee(assignee)
                .reporter(user)
                .createdBy(user)
                .priority(request.getPriority() != null ? request.getPriority() : com.taskmaster_springboot.model.enums.TaskPriority.MEDIUM)
                .startDate(request.getStartDate())
                .dueDate(request.getDueDate())
                .estimateMinutes(request.getEstimateMinutes())
                .tags(request.getTags())
                .archived(false)
                .build();

        // Generate task key
        long taskCount = taskRepository.countByProject(project);
        task.setKey(project.getKey() + "-" + (taskCount + 1));

        Tasks savedTask = taskRepository.save(task);
        log.info("Task created successfully: {}", savedTask.getId());

        return mapToDTO(savedTask);
    }

    @Override
    public TaskResponseDTO getTaskById(UUID taskId) {
        Tasks task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        return mapToDTO(task);
    }

    @Override
    public List<TaskResponseDTO> getProjectTasks(UUID projectId) {
        Projects project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        return taskRepository.findByProject(project).stream()
                .filter(task -> !task.getArchived())
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskResponseDTO> getUserAssignedTasks(String email) {
        Users user = usersRepository.findByEmail(email);
        if (user == null) {
            throw new AuthenticationException("User not found");
        }

        return taskRepository.findByAssignee(user).stream()
                .filter(task -> !task.getArchived())
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TaskResponseDTO updateTask(UUID taskId, TaskCreateRequestDTO request, String email) {
        Tasks task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        Users user = usersRepository.findByEmail(email);
        if (user == null) {
            throw new AuthenticationException("User not found");
        }

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority() != null ? request.getPriority() : task.getPriority());
        task.setStartDate(request.getStartDate());
        task.setDueDate(request.getDueDate());
        task.setEstimateMinutes(request.getEstimateMinutes());
        task.setTags(request.getTags());

        Tasks updatedTask = taskRepository.save(task);
        return mapToDTO(updatedTask);
    }

    @Override
    public void deleteTask(UUID taskId, String email) {
        Tasks task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        task.setArchived(true);
        taskRepository.save(task);
        log.info("Task archived: {}", taskId);
    }

    @Override
    public void assignTask(UUID taskId, UUID assigneeId, String email) {
        Tasks task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        Users assignee = usersRepository.findById(assigneeId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignee not found"));

        task.setAssignee(assignee);
        taskRepository.save(task);
        log.info("Task assigned to: {}", assigneeId);
    }

    @Override
    public void updateTaskStatus(UUID taskId, String status, String email) {
        Tasks task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        try {
            task.setStatus(TaskStatus.valueOf(status.toUpperCase()));
            taskRepository.save(task);
            log.info("Task status updated to: {}", status);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid task status: " + status);
        }
    }

    @Override
    public List<TaskResponseDTO> filterTasks(UUID projectId, String status, String priority, String assigneeId) {
        // Implementation for filtering tasks
        return List.of();
    }

    private TaskResponseDTO mapToDTO(Tasks task) {
        return TaskResponseDTO.builder()
                .id(task.getId())
                .projectId(task.getProject().getId())
                .key(task.getKey())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .assigneeId(task.getAssignee() != null ? task.getAssignee().getUserId() : null)
                .assigneeName(task.getAssignee() != null ? task.getAssignee().getFirstName() + " " + task.getAssignee().getLastName() : null)
                .reporterId(task.getReporter() != null ? task.getReporter().getUserId() : null)
                .startDate(task.getStartDate())
                .dueDate(task.getDueDate())
                .estimateMinutes(task.getEstimateMinutes())
                .timeSpentMinutes(task.getTimeSpentMinutes())
                .tags(task.getTags())
                .commentCount(task.getComments() != null ? task.getComments().size() : 0)
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .archived(task.getArchived())
                .build();
    }
}
