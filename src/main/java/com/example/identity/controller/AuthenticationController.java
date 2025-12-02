package com.example.identity.controller;

import java.text.ParseException;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.example.identity.dto.reponse.ApiResponse;
import com.example.identity.dto.reponse.AuthenticationResponse;
import com.example.identity.dto.reponse.IntrospectResponse;
import com.example.identity.dto.request.*;
import com.example.identity.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {

    AuthenticationService authenticationService;

    @PostMapping("/send-email")
    public ApiResponse<String> sendEmail(@RequestBody EmailSendRequest request) {
        authenticationService.sendVerificationCode(request.getEmail());
        return ApiResponse.<String>builder()
                .result("Verification code sent to " + request.getEmail())
                .build();
    }
    //
    //    @PostMapping("/verify-and-register")
    //    public ApiResponse<UserResponse> verifyAndRegister(@RequestBody @Valid UserCreationRequest request) {
    //        return ApiResponse.<UserResponse>builder()
    //                .result(authenticationService.register(request))
    //                .build();
    //    }

    @PostMapping("/log-in")
    ApiResponse<AuthenticationResponse> logIn(@RequestBody AuthenticationRequest authenticationRequest) {
        var result = authenticationService.authenticate(authenticationRequest);
        return ApiResponse.<AuthenticationResponse>builder().result(result).build();
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest introspectRequest)
            throws ParseException, JOSEException {
        var result = authenticationService.introspect(introspectRequest);
        return ApiResponse.<IntrospectResponse>builder().result(result).build();
    }

    @PostMapping("/logout")
    ApiResponse<Void> logout(@RequestBody LogoutRequest request) throws ParseException, JOSEException {
        authenticationService.logout(request);
        return ApiResponse.<Void>builder().build();
    }

    @PostMapping("/refresh")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody RefreshRequest refreshRequest)
            throws ParseException, JOSEException {
        var result = authenticationService.refreshToken(refreshRequest);
        return ApiResponse.<AuthenticationResponse>builder().result(result).build();
    }

    @PostMapping("/forgot-password")
    ApiResponse<Void> forgotPassword(@RequestBody @Valid EmailSendRequest request) {
        authenticationService.forgotPassword(request);
        return ApiResponse.<Void>builder()
                .message("Reset password email sent successfully.")
                .build();
    }

    @PostMapping("/reset-password")
    ApiResponse<Void> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        authenticationService.resetPassword(request);
        return ApiResponse.<Void>builder()
                .message("Password has been reset successfully.")
                .build();
    }
}
