package com.example.identity.controller;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import com.example.identity.dto.reponse.ApiResponse;
import com.example.identity.dto.reponse.UserResponse;
import com.example.identity.dto.request.*;
import com.example.identity.service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

    UserService userService;

    @PostMapping("/create-users")
    ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.createNewUser(request))
                .build();
    }

    @PutMapping("/update-users/{userID}")
    ApiResponse<UserResponse> updateUserForAdmin(
            @PathVariable("userID") String userID, @RequestBody @Valid UserUpdationRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateUserForAdminByID(userID, request))
                .build();
    }

    @PostMapping("/change-password/{userId}")
    ApiResponse<Void> changePassword(
            @PathVariable("userId") String userId, @RequestBody @Valid ChangePasswordRequest request) {
        userService.changePassword(userId, request);
        return ApiResponse.<Void>builder().message("Password has been changed").build();
    }

    @GetMapping
    public Page<UserResponse> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "") String search) {
        return userService.getUsers(page, size, search);
    }

    @GetMapping("/{userID}")
    UserResponse getUser(@PathVariable("userID") String userID) {
        return userService.getUserByID(userID);
    }

    @GetMapping("/my-info")
    ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }

    @DeleteMapping("/{userID}")
    ApiResponse<String> deleteUser(@PathVariable String userID) {
        userService.deleteUserByID(userID);
        return ApiResponse.<String>builder().result("User has been deleted").build();
    }
}
