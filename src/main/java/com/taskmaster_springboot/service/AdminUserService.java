package com.taskmaster_springboot.service;

import com.taskmaster_springboot.dto.response.AdminUserResponseDTO;
import com.taskmaster_springboot.model.enums.AccountStatus;
import com.taskmaster_springboot.model.enums.RoleName;

import java.util.List;
import java.util.UUID;

public interface AdminUserService {

    /**
     * Get all users (paginated)
     */
    List<AdminUserResponseDTO> getAllUsers();

    /**
     * Get user details by ID
     */
    AdminUserResponseDTO getUserById(UUID userId);

    /**
     * Update user role (change USER to MANAGER, etc.)
     */
    AdminUserResponseDTO updateUserRole(UUID userId, RoleName newRole);

    /**
     * Activate or deactivate user by changing account status
     */
    AdminUserResponseDTO updateUserStatus(UUID userId, AccountStatus status);

    /**
     * Delete user by ID
     */
    void deleteUser(UUID userId);
}
