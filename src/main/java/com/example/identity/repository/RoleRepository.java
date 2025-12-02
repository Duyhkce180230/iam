package com.example.identity.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.identity.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
    Role findByRoleName(String roleName);

    Optional<Role> findByRoleNameIgnoreCase(String roleName);

    Optional<Role> findByRoleId(String roleId);
}
