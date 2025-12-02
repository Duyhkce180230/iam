package com.example.identity.dto.request;

import jakarta.validation.constraints.NotBlank;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PermissionRequest {
    @NotBlank(message = "PERMISSION_NAME_REQUIRED")
    String permissionName;

    @NotBlank(message = "PERMISSION_DESCRIPTION_REQUIRED")
    String permissionDescription;
}
