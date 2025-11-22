package com.taskmaster_springboot.mapper;

import com.taskmaster_springboot.dto.request.UserCreateRequestDTO;
import com.taskmaster_springboot.dto.response.UserCreateResponseDTO;
import com.taskmaster_springboot.model.Users;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // Entity -> DTO
    UserCreateResponseDTO toUserCreateResponseDTO(Users user);

    // DTO -> Entity
    Users toUser(UserCreateRequestDTO userCreateRequestDTO);
}
