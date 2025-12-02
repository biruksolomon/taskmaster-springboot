package com.taskmaster_springboot.service.impl;

import com.taskmaster_springboot.dto.request.ProjectCreateRequestDTO;
import com.taskmaster_springboot.dto.response.ProjectResponseDTO;
import com.taskmaster_springboot.exceptions.AuthenticationException;
import com.taskmaster_springboot.exceptions.ResourceNotFoundException;
import com.taskmaster_springboot.model.Projects;
import com.taskmaster_springboot.model.Teams;
import com.taskmaster_springboot.model.Users;
import com.taskmaster_springboot.repository.ProjectMemberRepository;
import com.taskmaster_springboot.repository.ProjectRepository;
import com.taskmaster_springboot.repository.TeamsRepository;
import com.taskmaster_springboot.repository.UsersRepository;
import com.taskmaster_springboot.service.ProjectService;
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
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final TeamsRepository teamsRepository;
    private final UsersRepository usersRepository;
    private final ProjectMemberRepository projectMemberRepository;

    @Override
    public ProjectResponseDTO createProject(ProjectCreateRequestDTO request, String email) {
        log.info("Creating project: {}", request.getName());

        Users user = usersRepository.findByEmail(email);
        if (user == null) {
            throw new AuthenticationException("User not found");
        }

        Teams team = teamsRepository.findById(request.getTeamId())
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));

        Projects project = Projects.builder()
                .key(request.getKey())
                .name(request.getName())
                .description(request.getDescription())
                .team(team)
                .owner(user)
                .createdBy(user)
                .startDate(request.getStartDate())
                .dueDate(request.getDueDate())
                .visibility(request.getVisibility() != null ? request.getVisibility() : com.taskmaster_springboot.model.enums.ProjectVisibility.PRIVATE)
                .archived(false)
                .build();

        Projects savedProject = projectRepository.save(project);
        log.info("Project created successfully: {}", savedProject.getId());

        return mapToDTO(savedProject);
    }

    @Override
    public ProjectResponseDTO getProjectById(UUID projectId) {
        Projects project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        return mapToDTO(project);
    }

    @Override
    public List<ProjectResponseDTO> getTeamProjects(UUID teamId) {
        Teams team = teamsRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));

        return projectRepository.findByTeam(team).stream()
                .filter(project -> !project.getArchived())
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectResponseDTO> getUserProjects(String email) {
        Users user = usersRepository.findByEmail(email);
        if (user == null) {
            throw new AuthenticationException("User not found");
        }

        return projectRepository.findByOwner(user).stream()
                .filter(project -> !project.getArchived())
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProjectResponseDTO updateProject(UUID projectId, ProjectCreateRequestDTO request, String email) {
        Projects project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        Users user = usersRepository.findByEmail(email);
        if (user == null || !project.getOwner().getUserId().equals(user.getUserId())) {
            throw new AuthenticationException("Unauthorized to update this project");
        }

        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setStartDate(request.getStartDate());
        project.setDueDate(request.getDueDate());

        Projects updatedProject = projectRepository.save(project);
        return mapToDTO(updatedProject);
    }

    @Override
    public void deleteProject(UUID projectId, String email) {
        Projects project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        Users user = usersRepository.findByEmail(email);
        if (user == null || !project.getOwner().getUserId().equals(user.getUserId())) {
            throw new AuthenticationException("Unauthorized to delete this project");
        }

        project.setArchived(true);
        projectRepository.save(project);
        log.info("Project archived: {}", projectId);
    }

    @Override
    public void addProjectMember(UUID projectId, String memberEmail, String role, String email) {
        // Implementation for adding project member
    }

    @Override
    public void removeProjectMember(UUID projectId, String memberEmail, String email) {
        // Implementation for removing project member
    }

    private ProjectResponseDTO mapToDTO(Projects project) {
        return ProjectResponseDTO.builder()
                .id(project.getId())
                .key(project.getKey())
                .name(project.getName())
                .description(project.getDescription())
                .status(project.getStatus())
                .visibility(project.getVisibility())
                .ownerId(project.getOwner() != null ? project.getOwner().getUserId() : null)
                .teamId(project.getTeam() != null ? project.getTeam().getId() : null)
                .startDate(project.getStartDate())
                .dueDate(project.getDueDate())
                .taskCount(project.getTasks() != null ? project.getTasks().size() : 0)
                .memberCount(project.getProjectMembers() != null ? project.getProjectMembers().size() : 0)
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .archived(project.getArchived())
                .build();
    }
}
