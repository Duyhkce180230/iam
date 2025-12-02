package com.example.identity.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResetPasswordRequest {
    @NotBlank(message = "EMAIL_REQUIRED")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "EMAIL_INVALID")
    String email;

    @NotBlank(message = "VERIFIED_CODE_REQUIRED")
    String verificationCode;

    @NotBlank(message = "NEW_PASSWORD_REQUIRED")
    @Size(min = 7, max = 100, message = "PASSWORD_INVALID")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{7,100}$",
            message = "PASSWORD_WEAK")
    String newPassword;

    @NotBlank(message = "NEW_CONFIRM_PASSWORD_REQUIRED")
    String confirmNewPassword;
}
