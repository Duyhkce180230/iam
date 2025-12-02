package com.example.identity.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
    INVALID_KEY(1001, "Undefined exception", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1002, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1003, "You do not have permission to perform this action", HttpStatus.FORBIDDEN),
    UNDEFINED_EXCEPTION(1004, "Undefined exception", HttpStatus.INTERNAL_SERVER_ERROR),

    USER_NOT_FOUND(1010, "User not found", HttpStatus.NOT_FOUND),
    USER_EXISTED(1011, "User already existed", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1012, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    USERNAME_REQUIRED(1013, "Username is required", HttpStatus.BAD_REQUEST),

    INCORRECT_USER_OR_PASS(1020, "Incorrect username or password", HttpStatus.BAD_REQUEST),

    PASSWORD_INVALID(1021, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    PASSWORD_EXPIRED(1022, "Password has expired. Please reset your password.", HttpStatus.FORBIDDEN),
    INVALID_OLD_PASSWORD(1023, "Old password invalid", HttpStatus.BAD_REQUEST),
    PASSWORD_NOT_MATCH(1024, "Old Password and new password not match", HttpStatus.BAD_REQUEST),
    DUPLICATE_PASSWORD(1025, "Duplicate password", HttpStatus.BAD_REQUEST),
    PASSWORD_REQUIRED(1026, "Password is required", HttpStatus.BAD_REQUEST),
    PASSWORD_WEAK(
            1027, "Password must include uppercase, lowercase, number and special character", HttpStatus.BAD_REQUEST),
    NEW_PASSWORD_REQUIRED(1028, "New Password is required", HttpStatus.BAD_REQUEST),
    NEW_CONFIRM_PASSWORD_REQUIRED(1029, "New confirm Password is required", HttpStatus.BAD_REQUEST),
    OLD_PASSWORD_REQUIRED(1030, "Old Password is required", HttpStatus.BAD_REQUEST),

    ROLE_NOT_FOUND(1040, "Role not found", HttpStatus.NOT_FOUND),
    ROLE_EXISTED(1041, "Role already existed", HttpStatus.BAD_REQUEST),
    ROLE_NAME_REQUIRED(1042, "Role Name is required", HttpStatus.BAD_REQUEST),
    ROLE_CODE_REQUIRED(1043, "Role Code is required", HttpStatus.BAD_REQUEST),
    ROLE_DESCRIPTION_REQUIRED(1044, "Role Description is required", HttpStatus.BAD_REQUEST),

    PERMISSION_NOT_FOUND(1050, "Permission not found", HttpStatus.NOT_FOUND),
    PERMISSION_ALREADY_EXISTS(1051, "Permission already exists", HttpStatus.BAD_REQUEST),
    PERMISSION_NAME_REQUIRED(1052, "Permission Name is required", HttpStatus.BAD_REQUEST),
    PERMISSION_DESCRIPTION_REQUIRED(1053, "Permission description is required", HttpStatus.BAD_REQUEST),

    DOB_REQUIRED(1060, "Date of birth is required", HttpStatus.BAD_REQUEST),
    INVALID_DOB(1061, "Your age must be at least {min}", HttpStatus.BAD_REQUEST),

    VERIFIED_CODE_REQUIRED(1070, "Verification code is required", HttpStatus.BAD_REQUEST),
    INVALID_VERIFICATION_CODE(1071, "Invalid verification code", HttpStatus.BAD_REQUEST),

    EMAIL_NOT_FOUND(1080, "Email not found", HttpStatus.NOT_FOUND),
    EMAIL_REQUIRED(1081, "Email is required", HttpStatus.BAD_REQUEST),
    EMAIL_INVALID(1082, "Email format is invalid", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(1083, "Email already existed", HttpStatus.BAD_REQUEST),

    PHONE_REQUIRED(1090, "Phone number is required", HttpStatus.BAD_REQUEST),
    PHONE_INVALID(1091, "Phone number must start with 0 and have 10 digits", HttpStatus.BAD_REQUEST),

    FULLNAME_REQUIRED(1100, "Full name is required", HttpStatus.BAD_REQUEST),
    FULLNAME_INVALID(1101, "Full name must be between {min} and 100 characters", HttpStatus.BAD_REQUEST),

    IDENTIFY_REQUIRED(1110, "Identity number is required", HttpStatus.BAD_REQUEST),
    IDENTIFY_INVALID(1111, "Identity number must be 9 or 12 digits", HttpStatus.BAD_REQUEST),

    GENDER_REQUIRED(1120, "Gender is required", HttpStatus.BAD_REQUEST),

    ADDRESS_REQUIRED(1130, "Address is required", HttpStatus.BAD_REQUEST),
    ADDRESS_TOO_LONG(1131, "Address too long", HttpStatus.BAD_REQUEST),

    ACCOUNT_LOCKED(1140, "Account is temporarily locked due to multiple failed login attempts", HttpStatus.FORBIDDEN),
    ;

    ErrorCode(int code, String message, HttpStatusCode httpStatusCode) {
        this.code = code;
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }

    private final int code;
    private final HttpStatusCode httpStatusCode;
    private final String message;
}
