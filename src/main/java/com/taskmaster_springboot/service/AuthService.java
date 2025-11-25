package com.taskmaster_springboot.service;

import com.taskmaster_springboot.dto.response.AuthResponseDTO;

public interface AuthService {
    AuthResponseDTO authenticateUser(String email, String password);
    void generatePasswordResetToken(String email);
    void resetPassword(String token, String newPassword);
}
