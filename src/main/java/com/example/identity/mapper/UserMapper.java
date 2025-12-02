package com.example.identity.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.identity.dto.reponse.AdminResponse;
import com.example.identity.dto.reponse.UserResponse;
import com.example.identity.dto.request.*;
import com.example.identity.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "role", ignore = true)
    User toUser(UserCreationRequest userCreationRequest);

    UserResponse toUserResponse(User user);

    AdminResponse toAdminResponse(User admin);

    List<UserResponse> toUserResponse(List<User> users);

    @Mapping(target = "role", ignore = true)
    void updateUserAdmin(@MappingTarget User user, UserUpdationRequest userUpdateRequest);
}
