package com.example.identity.dto.reponse;

import com.example.identity.entity.Role;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminResponse {
    String userId;
    Role roles;
}
