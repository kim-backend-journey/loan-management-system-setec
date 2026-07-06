package com.lms.auth.dto.response;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        Long expiresIn,
        Integer userId,
        String username,
        String email,
        String role
) {}