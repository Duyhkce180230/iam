package com.example.identity.dto.reponse;

import java.util.Set;

import com.example.identity.enums.Permissions;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleResponse {
    String roleId;
    String roleName;
    String roleCode;
    String roleDescription;
    Set<Permissions> permissions;
}
