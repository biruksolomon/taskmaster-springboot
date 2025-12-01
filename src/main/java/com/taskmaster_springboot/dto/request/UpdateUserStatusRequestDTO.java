package com.taskmaster_springboot.dto.request;

import com.taskmaster_springboot.model.enums.AccountStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserStatusRequestDTO {
    @NotNull(message = "Status is required")
    private AccountStatus status;
}
