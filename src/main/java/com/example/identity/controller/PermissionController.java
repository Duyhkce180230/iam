package com.example.identity.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.example.identity.dto.reponse.ApiResponse;
import com.example.identity.dto.reponse.PermissionResponse;
import com.example.identity.dto.request.PermissionRequest;
import com.example.identity.service.PermissionService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/permissions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionController {

    PermissionService permissionService;

    @PostMapping
    ApiResponse<PermissionResponse> create(@RequestBody @Valid PermissionRequest request) {
        return ApiResponse.<PermissionResponse>builder()
                .result(permissionService.createPermission(request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<PermissionResponse>> getAll() {
        return ApiResponse.<List<PermissionResponse>>builder()
                .result(permissionService.getAllPermissions())
                .build();
    }

    @PutMapping("/{permission}")
    ApiResponse<PermissionResponse> update(
            @PathVariable("permission") String permission, @RequestBody @Valid PermissionRequest request) {
        return ApiResponse.<PermissionResponse>builder()
                .result(permissionService.updatePermission(permission, request))
                .build();
    }

    @DeleteMapping("/{permission}")
    ApiResponse<String> delete(@PathVariable String permission) {
        permissionService.deletePermission(permission);
        return ApiResponse.<String>builder().result("Permission deleted").build();
    }
}
