package com.taskmaster_springboot.dto.response;

import com.taskmaster_springboot.model.enums.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAuthResponseDTO {
    private UUID userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String avatarUrl;
    private AccountStatus status;
    private Boolean emailVerified;
    private Instant createdAt;
    private Instant lastLoginAt;
}
