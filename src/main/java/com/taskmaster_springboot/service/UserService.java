package com.taskmaster_springboot.service;

import com.taskmaster_springboot.dto.request.UserCreateRequestDTO;
import com.taskmaster_springboot.dto.response.UserCreateResponseDTO;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    UserCreateResponseDTO createUser(UserCreateRequestDTO userCreateRequestDTO);
    Boolean verifyEmailCode(String email ,String code);
}
