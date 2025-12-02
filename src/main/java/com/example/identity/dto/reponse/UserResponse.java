package com.example.identity.dto.reponse;

import java.time.LocalDate;

import com.example.identity.entity.Role;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {

    String userId;

    String username;

    LocalDate dob;

    Role role;

    String email;

    String phone;

    String fullName;

    String identifyNumber;

    Boolean gender;

    String address;

    Boolean isFirstLogin;
}
