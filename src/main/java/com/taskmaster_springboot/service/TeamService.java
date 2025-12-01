package com.taskmaster_springboot.service;

import com.taskmaster_springboot.dto.request.TeamCreateRequestDTO;
import com.taskmaster_springboot.dto.response.TeamResponseDTO;

import java.util.List;
import java.util.UUID;

public interface TeamService {
    TeamResponseDTO createTeam(TeamCreateRequestDTO request, String email);
    TeamResponseDTO getTeamById(UUID teamId);
    List<TeamResponseDTO> getUserTeams(String email);
    TeamResponseDTO updateTeam(UUID teamId, TeamCreateRequestDTO request, String email);
    void deleteTeam(UUID teamId, String email);
    void addTeamMember(UUID teamId, String memberEmail, String role, String email);
    void removeTeamMember(UUID teamId, String memberEmail, String email);
    List<TeamResponseDTO> getAllTeams();
}
