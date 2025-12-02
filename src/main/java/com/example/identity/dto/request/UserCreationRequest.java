package com.example.identity.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.*;

import com.example.identity.validator.DobContraint;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {

    @NotNull(message = "DOB_REQUIRED")
    @DobContraint(min = 12, message = "INVALID_DOB")
    //    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    LocalDate dob;

    String role;

    @NotBlank(message = "EMAIL_REQUIRED")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "EMAIL_INVALID")
    String email;

    @NotBlank(message = "PHONE_REQUIRED")
    @Pattern(regexp = "^0\\d{9}$", message = "PHONE_INVALID")
    String phone;

    @NotBlank(message = "FULLNAME_REQUIRED")
    @Size(min = 4, max = 100, message = "FULLNAME_INVALID")
    String fullName;

    @NotBlank(message = "IDENTIFY_REQUIRED")
    @Pattern(regexp = "^(\\d{9}|\\d{12})$", message = "IDENTIFY_INVALID")
    String identifyNumber;

    @NotNull(message = "GENDER_REQUIRED")
    Boolean gender;

    @NotBlank(message = "ADDRESS_REQUIRED")
    @Size(max = 255, message = "ADDRESS_TOO_LONG")
    String address;
}
