package com.taskmaster_springboot.service;

import com.taskmaster_springboot.dto.request.UserCreateRequestDTO;
import com.taskmaster_springboot.dto.response.UserCreateResponseDTO;

@org.springframework.stereotype.Service
public interface UserService {
    UserCreateResponseDTO createUser(UserCreateRequestDTO userCreateRequestDTO);
    Boolean verifyEmailCode(String email, String code);
    void updateLastLogin(String email);
}
