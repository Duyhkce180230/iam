package com.example.identity.mapper;

import com.example.identity.dto.reponse.RoleResponse;
import com.example.identity.dto.request.RoleRequest;
import com.example.identity.entity.Role;
import com.example.identity.enums.Permissions;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Oracle Corporation)"
)
@Component
public class RoleMapperImpl implements RoleMapper {

    @Override
    public Role toRole(RoleRequest roleRequest) {
        if ( roleRequest == null ) {
            return null;
        }

        Role.RoleBuilder role = Role.builder();

        role.roleName( roleRequest.getRoleName() );
        role.roleCode( roleRequest.getRoleCode() );
        role.roleDescription( roleRequest.getRoleDescription() );

        return role.build();
    }

    @Override
    public RoleResponse toRoleResponse(Role role) {
        if ( role == null ) {
            return null;
        }

        RoleResponse.RoleResponseBuilder roleResponse = RoleResponse.builder();

        roleResponse.roleId( role.getRoleId() );
        roleResponse.roleName( role.getRoleName() );
        roleResponse.roleCode( role.getRoleCode() );
        roleResponse.roleDescription( role.getRoleDescription() );
        Set<Permissions> set = role.getPermissions();
        if ( set != null ) {
            roleResponse.permissions( new LinkedHashSet<Permissions>( set ) );
        }

        return roleResponse.build();
    }

    @Override
    public void updateRole(Role role, RoleRequest roleRequest) {
        if ( roleRequest == null ) {
            return;
        }

        role.setRoleName( roleRequest.getRoleName() );
        role.setRoleCode( roleRequest.getRoleCode() );
        role.setRoleDescription( roleRequest.getRoleDescription() );
    }
}
