package com.taskmaster_springboot.service.impl;

import com.taskmaster_springboot.dto.request.UserCreateRequestDTO;
import com.taskmaster_springboot.dto.response.UserCreateResponseDTO;
import com.taskmaster_springboot.mapper.UserMapper;
import com.taskmaster_springboot.model.Roles;
import com.taskmaster_springboot.model.Users;
import com.taskmaster_springboot.model.enums.RoleName;
import com.taskmaster_springboot.repository.RolesRepository;
import com.taskmaster_springboot.repository.UsersRepository;
import com.taskmaster_springboot.service.EmailService;
import com.taskmaster_springboot.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;

import java.util.Set;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {


    private UserMapper userMapper;

    private EmailService emailService;

    private  UsersRepository usersRepository;

    private  RolesRepository rolesRepository;

    private PasswordEncoder passwordEncoder;



    @Override
    public UserCreateResponseDTO createUser(UserCreateRequestDTO userCreateRequestDTO) {

        if (usersRepository.existsByEmail(userCreateRequestDTO.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        Users user = userMapper.toUser(userCreateRequestDTO);

        // Generate secure 4-digit code
        SecureRandom secureRandom = new SecureRandom();
        String code = String.format("%04d", secureRandom.nextInt(10000));

        user.setEmailVerificationCode(code);
        user.setEmailVerificationCodeExpireAt(Instant.now().plusSeconds(1200));
        user.setPassword(passwordEncoder.encode(user.getPassword()));



        Roles userRole = rolesRepository.findByName(RoleName.USER)
                .orElseThrow(() -> new RuntimeException("Role USER not found"));

        user.setRoles(Set.of(userRole));

        Users savedUser = usersRepository.save(user);

        emailService.sendVerificationEmail(user.getEmail(), code);

        return userMapper.toUserCreateResponseDTO(savedUser);
    }



    @Override
    public Boolean verifyEmailCode(String email, String code) {

        Users user = usersRepository.findByEmail(email);

        if (user == null) {
            throw new IllegalArgumentException("Email not found.");
        }

        if (!code.equals(user.getEmailVerificationCode())) {
            throw new IllegalArgumentException("Invalid verification code.");
        }

        // Check expiration
        if (user.getEmailVerificationCodeExpireAt().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Verification code expired.");
        }

        user.setEmailVerified(true);

        // Clear code after use
        user.setEmailVerificationCode(null);
        user.setEmailVerificationCodeExpireAt(null);

        usersRepository.save(user);

        return true;
    }

    @Override
    public void updateLastLogin(String email) {
        Users user = usersRepository.findByEmail(email);

        if (user == null) {
            throw new IllegalArgumentException("User not found with email: " + email);
        }

        user.setLastLoginAt(Instant.now());
        usersRepository.save(user);
    }


}
