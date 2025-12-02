package com.example.identity.dto.request;

import jakarta.validation.constraints.NotBlank;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangePasswordRequest {
    @NotBlank(message = "OLD_PASSWORD_REQUIRED")
    String oldPassword;

    @NotBlank(message = "NEW_PASSWORD_REQUIRED")
    String newPassword;

    @NotBlank(message = "NEW_CONFIRM_PASSWORD_REQUIRED")
    String confirmNewPassword;
}
