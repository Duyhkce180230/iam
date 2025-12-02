package com.example.identity.mapper;

import com.example.identity.dto.reponse.PermissionResponse;
import com.example.identity.dto.request.PermissionRequest;
import com.example.identity.entity.Permission;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Oracle Corporation)"
)
@Component
public class PermissionMapperImpl implements PermissionMapper {

    @Override
    public Permission toPermission(PermissionRequest permissionRequest) {
        if ( permissionRequest == null ) {
            return null;
        }

        Permission.PermissionBuilder permission = Permission.builder();

        permission.permissionName( permissionRequest.getPermissionName() );
        permission.permissionDescription( permissionRequest.getPermissionDescription() );

        return permission.build();
    }

    @Override
    public PermissionResponse toPermissionResponse(Permission permission) {
        if ( permission == null ) {
            return null;
        }

        PermissionResponse.PermissionResponseBuilder permissionResponse = PermissionResponse.builder();

        permissionResponse.permissionName( permission.getPermissionName() );

        return permissionResponse.build();
    }

    @Override
    public void updatePermission(Permission permission, PermissionRequest permissionRequest) {
        if ( permissionRequest == null ) {
            return;
        }

        permission.setPermissionName( permissionRequest.getPermissionName() );
        permission.setPermissionDescription( permissionRequest.getPermissionDescription() );
    }
}
