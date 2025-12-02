package com.example.identity.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.example.identity.dto.reponse.RoleResponse;
import com.example.identity.dto.request.RoleRequest;
import com.example.identity.entity.Role;
import com.example.identity.exception.AppException;
import com.example.identity.exception.ErrorCode;
import com.example.identity.mapper.RoleMapper;
import com.example.identity.repository.RoleRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RoleService {
    RoleRepository roleRepository;
    RoleMapper roleMapper;

    @PreAuthorize("hasAuthority('CREATE_ROLE')")
    public RoleResponse create(RoleRequest request) {
        var role = roleMapper.toRole(request);
        if (roleRepository.findByRoleNameIgnoreCase(request.getRoleName()).isPresent()) {
            throw new AppException(ErrorCode.ROLE_EXISTED);
        }

        // ✅ Gán quyền trực tiếp từ enum
        if (request.getPermissions() != null) {
            role.setPermissions(request.getPermissions());
        }

        roleRepository.save(role);
        return roleMapper.toRoleResponse(role);
    }

    @PreAuthorize("hasAuthority('VIEW_ROLE')")
    public List<RoleResponse> getAll() {
        return roleRepository.findAll().stream().map(roleMapper::toRoleResponse).toList();
    }

    @PreAuthorize("hasAuthority('UPDATE_ROLE')")
    public RoleResponse updateRole(String roleId, RoleRequest request) {
        Role role = roleRepository.findById(roleId).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        roleMapper.updateRole(role, request);

        // ✅ Gán trực tiếp vì đã là enum
        if (request.getPermissions() != null) {
            role.setPermissions(request.getPermissions());
        }

        return roleMapper.toRoleResponse(roleRepository.save(role));
    }

    @PreAuthorize("hasAuthority('DELETE_ROLE')")
    public void delete(String roleName) {
        Role role = roleRepository.findById(roleName).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
        roleRepository.delete(role);
    }
}
