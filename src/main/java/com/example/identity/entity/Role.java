package com.example.identity.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;

import com.example.identity.enums.Permissions;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String roleId;

    String roleName;

    String roleCode;

    String roleDescription;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "role_permissions",
            joinColumns = @JoinColumn(name = "role_id") // ✅ chỉ định rõ tên cột khóa ngoại
            )
    @Enumerated(EnumType.STRING)
    @Column(name = "permission")
    private Set<Permissions> permissions = new HashSet<>();
}
