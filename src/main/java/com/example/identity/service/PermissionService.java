package com.example.identity.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.example.identity.dto.reponse.PermissionResponse;
import com.example.identity.dto.request.PermissionRequest;
import com.example.identity.entity.Permission;
import com.example.identity.entity.Role;
import com.example.identity.enums.Permissions;
import com.example.identity.exception.AppException;
import com.example.identity.exception.ErrorCode;
import com.example.identity.mapper.PermissionMapper;
import com.example.identity.repository.PermissionRepository;
import com.example.identity.repository.RoleRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class PermissionService {
    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;
    RoleRepository roleRepository;

    public PermissionResponse createPermission(PermissionRequest request) {
        permissionRepository.findByPermissionName(request.getPermissionName()).ifPresent(p -> {
            throw new AppException(ErrorCode.PERMISSION_ALREADY_EXISTS);
        });
        Permission permission = permissionMapper.toPermission(request);
        return permissionMapper.toPermissionResponse(permissionRepository.save(permission));
    }

    public List<PermissionResponse> getAllPermissions() {
        return Arrays.stream(Permissions.values())
                .map(p -> new PermissionResponse(p.name(), p.getDisplayName()))
                .collect(Collectors.toList());
    }

    public PermissionResponse updatePermission(String permissionId, PermissionRequest request) {
        Permission permission = permissionRepository
                .findById(permissionId)
                .orElseThrow(() -> new AppException(ErrorCode.PERMISSION_NOT_FOUND));

        permissionRepository.findByPermissionName(request.getPermissionName()).ifPresent(existing -> {
            if (!existing.getPermissionId().equals(permissionId)) {
                throw new AppException(ErrorCode.PERMISSION_ALREADY_EXISTS);
            }
        });

        permissionMapper.updatePermission(permission, request);
        return permissionMapper.toPermissionResponse(permissionRepository.save(permission));
    }

    @Transactional
    public void deletePermission(String id) {
        Permission permission =
                permissionRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.PERMISSION_NOT_FOUND));

        // Gỡ permission khỏi tất cả role đang có nó
        List<Role> roles = roleRepository.findAll();
        for (Role role : roles) {
            role.getPermissions().remove(permission);
        }
        roleRepository.saveAll(roles);

        permissionRepository.delete(permission);
    }
}
