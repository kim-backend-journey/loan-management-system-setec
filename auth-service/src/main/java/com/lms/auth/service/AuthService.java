package com.lms.auth.service;

import com.lms.auth.dto.request.LoginRequest;
import com.lms.auth.dto.request.RefreshTokenRequest;
import com.lms.auth.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse login(LoginRequest request, String ipAddress, String userAgent);

    AuthResponse refresh(RefreshTokenRequest request);

    void logout(String refreshToken);
}