package com.taskmaster_springboot.dto.response;


import com.taskmaster_springboot.model.enums.AccountStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateResponseDTO {
    private UUID userId;
    private String username;
    private String firstName;
    private String lastName;
    @Enumerated(EnumType.STRING)
    private AccountStatus status;
    private Boolean emailVerified;
    private String avatarUrl;
    private String metadata;
    private Instant createdAt;

}
