package com.taskmaster_springboot.service.impl;

import com.taskmaster_springboot.dto.response.AdminUserResponseDTO;
import com.taskmaster_springboot.exceptions.ResourceNotFoundException;
import com.taskmaster_springboot.mapper.UserMapper;
import com.taskmaster_springboot.model.Roles;
import com.taskmaster_springboot.model.Users;
import com.taskmaster_springboot.model.enums.AccountStatus;
import com.taskmaster_springboot.model.enums.RoleName;
import com.taskmaster_springboot.repository.RolesRepository;
import com.taskmaster_springboot.repository.UsersRepository;
import com.taskmaster_springboot.service.AdminUserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class AdminUserServiceImpl implements AdminUserService {

    private UsersRepository usersRepository;
    private RolesRepository rolesRepository;
    private UserMapper userMapper;

    @Override
    public List<AdminUserResponseDTO> getAllUsers() {
        log.info("Fetching all users");

        return usersRepository.findAll().stream()
                .map(userMapper::toAdminUserResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AdminUserResponseDTO getUserById(UUID userId) {
        log.info("Fetching user details for userId: {}", userId);

        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", userId);
                    return new ResourceNotFoundException("User not found with ID: " + userId);
                });

        return userMapper.toAdminUserResponseDTO(user);
    }

    @Override
    public AdminUserResponseDTO updateUserRole(UUID userId, RoleName newRole) {
        log.info("Updating user role for userId: {} to role: {}", userId, newRole);

        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", userId);
                    return new ResourceNotFoundException("User not found with ID: " + userId);
                });

        Roles role = rolesRepository.findByName(newRole)
                .orElseThrow(() -> {
                    log.error("Role not found: {}", newRole);
                    return new RuntimeException("Role not found: " + newRole);
                });

        // Replace all roles with the new role
        user.setRoles(Set.of(role));
        Users updatedUser = usersRepository.save(user);

        log.info("User role updated successfully for userId: {}", userId);
        return userMapper.toAdminUserResponseDTO(updatedUser);
    }

    @Override
    public AdminUserResponseDTO updateUserStatus(UUID userId, AccountStatus status) {
        log.info("Updating user status for userId: {} to status: {}", userId, status);

        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", userId);
                    return new ResourceNotFoundException("User not found with ID: " + userId);
                });

        // Prevent deactivating users with ADMIN role
        boolean hasAdminRole = user.getRoles().stream()
                .anyMatch(role -> role.getName() == RoleName.ADMIN);

        if (hasAdminRole && status == AccountStatus.DISABLED) {
            log.warn("Attempted to disable ADMIN user: {}", userId);
            throw new IllegalArgumentException("Cannot disable user with ADMIN role");
        }

        user.setStatus(status);
        Users updatedUser = usersRepository.save(user);

        log.info("User status updated successfully for userId: {}", userId);
        return userMapper.toAdminUserResponseDTO(updatedUser);
    }

    @Override
    public void deleteUser(UUID userId) {
        log.info("Deleting user with userId: {}", userId);

        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", userId);
                    return new ResourceNotFoundException("User not found with ID: " + userId);
                });

        // Prevent deleting users with ADMIN role
        boolean hasAdminRole = user.getRoles().stream()
                .anyMatch(role -> role.getName() == RoleName.ADMIN);

        if (hasAdminRole) {
            log.warn("Attempted to delete ADMIN user: {}", userId);
            throw new IllegalArgumentException("Cannot delete user with ADMIN role");
        }

        usersRepository.delete(user);
        log.info("User deleted successfully with userId: {}", userId);
    }
}
