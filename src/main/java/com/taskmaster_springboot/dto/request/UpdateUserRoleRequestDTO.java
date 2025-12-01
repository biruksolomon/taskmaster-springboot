package com.taskmaster_springboot.dto.request;

import com.taskmaster_springboot.model.enums.RoleName;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRoleRequestDTO {
    @NotNull(message = "Role is required")
    private RoleName role;
}
