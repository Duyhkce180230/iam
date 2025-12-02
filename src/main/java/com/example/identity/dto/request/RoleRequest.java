package com.example.identity.dto.request;

import java.util.Set;

import jakarta.validation.constraints.NotBlank;

import com.example.identity.enums.Permissions;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleRequest {
    @NotBlank(message = "ROLE_NAME_REQUIRED")
    String roleName;

    @NotBlank(message = "ROLE_CODE_REQUIRED")
    String roleCode;

    @NotBlank(message = "ROLE_DESCRIPTION_REQUIRED")
    String roleDescription;

    Set<Permissions> permissions;
}
