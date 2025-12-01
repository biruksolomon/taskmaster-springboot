package com.taskmaster_springboot.service.impl;

import com.taskmaster_springboot.dto.request.TeamCreateRequestDTO;
import com.taskmaster_springboot.dto.response.TeamResponseDTO;
import com.taskmaster_springboot.exceptions.AuthenticationException;
import com.taskmaster_springboot.exceptions.ResourceNotFoundException;
import com.taskmaster_springboot.model.Teams;
import com.taskmaster_springboot.model.Users;
import com.taskmaster_springboot.repository.TeamMembersRepository;
import com.taskmaster_springboot.repository.TeamsRepository;
import com.taskmaster_springboot.repository.UsersRepository;
import com.taskmaster_springboot.service.TeamService;
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
public class TeamServiceImpl implements TeamService {

    private final TeamsRepository teamsRepository;
    private final UsersRepository usersRepository;
    private final TeamMembersRepository teamMembersRepository;

    @Override
    public TeamResponseDTO createTeam(TeamCreateRequestDTO request, String email) {
        log.info("Creating team: {}", request.getName());

        Users user = usersRepository.findByEmail(email);
        if (user == null) {
            throw new AuthenticationException("User not found");
        }

        Teams team = Teams.builder()
                .name(request.getName())
                .slug(request.getSlug())
                .description(request.getDescription())
                .createdBy(user)
                .archived(false)
                .build();

        Teams savedTeam = teamsRepository.save(team);
        log.info("Team created successfully: {}", savedTeam.getId());

        return mapToDTO(savedTeam);
    }

    @Override
    public TeamResponseDTO getTeamById(UUID teamId) {
        Teams team = teamsRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));
        return mapToDTO(team);
    }

    @Override
    public List<TeamResponseDTO> getUserTeams(String email) {
        Users user = usersRepository.findByEmail(email);
        if (user == null) {
            throw new AuthenticationException("User not found");
        }

        return teamMembersRepository.findByUserId(user.getUserId())
                .stream()
                .map(tm -> mapToDTO(tm.getTeam()))
                .collect(Collectors.toList());
    }

    @Override
    public TeamResponseDTO updateTeam(UUID teamId, TeamCreateRequestDTO request, String email) {
        Teams team = teamsRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));

        Users user = usersRepository.findByEmail(email);
        if (user == null || !team.getCreatedBy().getUserId().equals(user.getUserId())) {
            throw new AuthenticationException("Unauthorized to update this team");
        }

        team.setName(request.getName());
        team.setSlug(request.getSlug());
        team.setDescription(request.getDescription());

        Teams updatedTeam = teamsRepository.save(team);
        return mapToDTO(updatedTeam);
    }

    @Override
    public void deleteTeam(UUID teamId, String email) {
        Teams team = teamsRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));

        Users user = usersRepository.findByEmail(email);
        if (user == null || !team.getCreatedBy().getUserId().equals(user.getUserId())) {
            throw new AuthenticationException("Unauthorized to delete this team");
        }

        team.setArchived(true);
        teamsRepository.save(team);
        log.info("Team archived: {}", teamId);
    }

    @Override
    public void addTeamMember(UUID teamId, String memberEmail, String role, String email) {
        // Implementation for adding team member
    }

    @Override
    public void removeTeamMember(UUID teamId, String memberEmail, String email) {
        // Implementation for removing team member
    }

    @Override
    public List<TeamResponseDTO> getAllTeams() {
        return teamsRepository.findAll().stream()
                .filter(team -> !team.getArchived())
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private TeamResponseDTO mapToDTO(Teams team) {
        return TeamResponseDTO.builder()
                .id(team.getId())
                .name(team.getName())
                .slug(team.getSlug())
                .description(team.getDescription())
                .createdById(team.getCreatedBy() != null ? team.getCreatedBy().getUserId() : null)
                .memberCount(team.getMembers() != null ? team.getMembers().size() : 0)
                .createdAt(team.getCreatedAt())
                .updatedAt(team.getUpdatedAt())
                .archived(team.getArchived())
                .build();
    }
}
