package com.taskmaster_springboot.service;

import com.taskmaster_springboot.dto.request.ProjectCreateRequestDTO;
import com.taskmaster_springboot.dto.response.ProjectResponseDTO;

import java.util.List;
import java.util.UUID;

public interface ProjectService {
    ProjectResponseDTO createProject(ProjectCreateRequestDTO request, String email);
    ProjectResponseDTO getProjectById(UUID projectId);
    List<ProjectResponseDTO> getTeamProjects(UUID teamId);
    List<ProjectResponseDTO> getUserProjects(String email);
    ProjectResponseDTO updateProject(UUID projectId, ProjectCreateRequestDTO request, String email);
    void deleteProject(UUID projectId, String email);
    void addProjectMember(UUID projectId, String memberEmail, String role, String email);
    void removeProjectMember(UUID projectId, String memberEmail, String email);
}
