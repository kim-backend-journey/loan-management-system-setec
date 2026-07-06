package com.lms.auth.dto.response;

import java.time.LocalDateTime;

public record UserResponse(
        Integer userId,
        String username,
        String email,
        String phoneNumber,
        String firstName,
        String lastName,
        String role,
        String status,
        Boolean emailVerified,
        Boolean phoneVerified,
        LocalDateTime lastLoginAt,
        LocalDateTime createdAt
) {}