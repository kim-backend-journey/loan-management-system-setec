package com.lms.auth.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Auth errors
    INVALID_CREDENTIALS("INVALID_CREDENTIALS", "Invalid username or password", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED("TOKEN_EXPIRED", "Token has expired", HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID("TOKEN_INVALID", "Token is invalid", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_REVOKED("REFRESH_TOKEN_REVOKED", "Refresh token has been revoked", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED("ACCESS_DENIED", "You do not have permission to access this resource", HttpStatus.FORBIDDEN),

    // User errors
    USER_NOT_FOUND("USER_NOT_FOUND", "User not found", HttpStatus.NOT_FOUND),
    USER_ALREADY_EXISTS("USER_ALREADY_EXISTS", "Username or email already exists", HttpStatus.CONFLICT),
    WRONG_PASSWORD("WRONG_PASSWORD", "Current password is incorrect", HttpStatus.BAD_REQUEST),
    PASSWORD_MISMATCH("PASSWORD_MISMATCH", "New password and confirm password do not match", HttpStatus.BAD_REQUEST),
    EMAIL_ALREADY_EXISTS("EMAIL_ALREADY_EXISTS", "Email already in use", HttpStatus.CONFLICT),
    PHONE_ALREADY_EXISTS("PHONE_ALREADY_EXISTS", "Phone number already in use", HttpStatus.CONFLICT),
    ACCOUNT_DISABLED("ACCOUNT_DISABLED", "Account is disabled", HttpStatus.FORBIDDEN),

    // Role errors
    ROLE_NOT_FOUND("ROLE_NOT_FOUND", "Role not found", HttpStatus.NOT_FOUND),

    // General errors
    VALIDATION_FAILED("VALIDATION_FAILED", "Request validation failed", HttpStatus.BAD_REQUEST),
    INTERNAL_ERROR("INTERNAL_ERROR", "An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String defaultReason;
    private final HttpStatus httpStatus;
}