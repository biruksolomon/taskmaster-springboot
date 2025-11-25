package com.taskmaster_springboot.mapper;

import com.taskmaster_springboot.dto.response.UserAuthResponseDTO;
import com.taskmaster_springboot.model.Users;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthMapper {

    // Entity -> DTO for authentication responses
    UserAuthResponseDTO toUserAuthResponseDTO(Users user);
}
