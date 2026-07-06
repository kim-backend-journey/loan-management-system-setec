package com.lms.auth.service;

public interface JwtService {

    String generateToken(Integer userId, String username, String role);

    String extractUsername(String token);

    Integer extractUserId(String token);

    String extractRole(String token);

    boolean isTokenValid(String token);
}