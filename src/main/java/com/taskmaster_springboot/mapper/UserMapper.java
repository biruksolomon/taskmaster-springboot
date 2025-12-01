package com.taskmaster_springboot.mapper;

import com.taskmaster_springboot.dto.request.UserCreateRequestDTO;
import com.taskmaster_springboot.dto.response.AdminUserResponseDTO;
import com.taskmaster_springboot.dto.response.UserCreateResponseDTO;
import com.taskmaster_springboot.model.Users;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // Entity -> DTO
    UserCreateResponseDTO toUserCreateResponseDTO(Users user);

    // DTO -> Entity
    Users toUser(UserCreateRequestDTO userCreateRequestDTO);

    @Mapping(target = "roles", expression = "java(mapRolesToStringSet(user.getRoles()))")
    AdminUserResponseDTO toAdminUserResponseDTO(Users user);

    default java.util.Set<String> mapRolesToStringSet(java.util.Set<com.taskmaster_springboot.model.Roles> roles) {
        return roles.stream()
                .map(role -> role.getName().toString())
                .collect(java.util.stream.Collectors.toSet());
    }
}
