package com.example.identity.mapper;

import com.example.identity.dto.reponse.AdminResponse;
import com.example.identity.dto.reponse.UserResponse;
import com.example.identity.dto.request.UserCreationRequest;
import com.example.identity.dto.request.UserUpdationRequest;
import com.example.identity.entity.User;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User toUser(UserCreationRequest userCreationRequest) {
        if ( userCreationRequest == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.email( userCreationRequest.getEmail() );
        user.phone( userCreationRequest.getPhone() );
        user.fullName( userCreationRequest.getFullName() );
        user.identifyNumber( userCreationRequest.getIdentifyNumber() );
        user.gender( userCreationRequest.getGender() );
        user.address( userCreationRequest.getAddress() );
        user.dob( userCreationRequest.getDob() );

        return user.build();
    }

    @Override
    public UserResponse toUserResponse(User user) {
        if ( user == null ) {
            return null;
        }

        UserResponse.UserResponseBuilder userResponse = UserResponse.builder();

        userResponse.userId( user.getUserId() );
        userResponse.username( user.getUsername() );
        userResponse.dob( user.getDob() );
        userResponse.role( user.getRole() );
        userResponse.email( user.getEmail() );
        userResponse.phone( user.getPhone() );
        userResponse.fullName( user.getFullName() );
        userResponse.identifyNumber( user.getIdentifyNumber() );
        userResponse.gender( user.getGender() );
        userResponse.address( user.getAddress() );
        userResponse.isFirstLogin( user.getIsFirstLogin() );

        return userResponse.build();
    }

    @Override
    public AdminResponse toAdminResponse(User admin) {
        if ( admin == null ) {
            return null;
        }

        AdminResponse.AdminResponseBuilder adminResponse = AdminResponse.builder();

        adminResponse.userId( admin.getUserId() );

        return adminResponse.build();
    }

    @Override
    public List<UserResponse> toUserResponse(List<User> users) {
        if ( users == null ) {
            return null;
        }

        List<UserResponse> list = new ArrayList<UserResponse>( users.size() );
        for ( User user : users ) {
            list.add( toUserResponse( user ) );
        }

        return list;
    }

    @Override
    public void updateUserAdmin(User user, UserUpdationRequest userUpdateRequest) {
        if ( userUpdateRequest == null ) {
            return;
        }

        user.setEmail( userUpdateRequest.getEmail() );
        user.setPhone( userUpdateRequest.getPhone() );
        user.setFullName( userUpdateRequest.getFullName() );
        user.setIdentifyNumber( userUpdateRequest.getIdentifyNumber() );
        user.setGender( userUpdateRequest.getGender() );
        user.setAddress( userUpdateRequest.getAddress() );
        user.setDob( userUpdateRequest.getDob() );
    }
}
