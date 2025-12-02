package com.example.identity.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.example.identity.dto.reponse.ApiResponse;
import com.example.identity.dto.reponse.RoleResponse;
import com.example.identity.dto.request.RoleRequest;
import com.example.identity.service.RoleService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {

    RoleService roleService;

    @PostMapping
    ApiResponse<RoleResponse> create(@RequestBody @Valid RoleRequest request) {
        return ApiResponse.<RoleResponse>builder()
                .result(roleService.create(request))
                .build();
    }

    @PutMapping("/{id}")
    ApiResponse<RoleResponse> updateRole(@PathVariable("id") String id, @RequestBody @Valid RoleRequest request) {
        return ApiResponse.<RoleResponse>builder()
                .result(roleService.updateRole(id, request))
                .build();
    }

    @GetMapping
    ApiResponse<List<RoleResponse>> getAll() {
        return ApiResponse.<List<RoleResponse>>builder()
                .result(roleService.getAll())
                .build();
    }

    @DeleteMapping("/{role}")
    ApiResponse<String> delete(@PathVariable String role) {
        roleService.delete(role);
        return ApiResponse.<String>builder().result("Role has been deleted").build();
    }
}
