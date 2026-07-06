package com.lms.auth.service;

import com.lms.auth.domain.*;
import com.lms.auth.dto.request.LoginRequest;
import com.lms.auth.dto.request.RefreshTokenRequest;
import com.lms.auth.dto.response.AuthResponse;
import com.lms.auth.exception.AppException;
import com.lms.auth.exception.ErrorCode;
import com.lms.auth.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final LoginHistoryRepository loginHistoryRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request,
                              String ipAddress,
                              String userAgent) {
        // 1. Authenticate
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            userRepository.findByUsername(request.getUsername())
                    .ifPresent(user -> saveLoginHistory(
                            user, request.getUsername(),
                            ipAddress, userAgent, "Failed"));
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }

        // 2. Load user
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // 3. Update last login
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        // 4. Generate tokens
        String accessToken = jwtService.generateToken(
                user.getUserId(), user.getUsername(), user.getRoleName());
        String refreshToken = generateAndSaveRefreshToken(user);

        // 5. Save login history
        saveLoginHistory(user, request.getUsername(),
                ipAddress, userAgent, "Success");

        return new AuthResponse(
                accessToken,
                refreshToken,
                "Bearer",
                900L,
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getRoleName()
        );
    }

    @Override
    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository
                .findByToken(request.getRefreshToken())
                .orElseThrow(() -> new AppException(ErrorCode.TOKEN_INVALID));

        if (refreshToken.getRevoked() ||
                refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.REFRESH_TOKEN_REVOKED);
        }

        User user = refreshToken.getUser();
        String newAccessToken = jwtService.generateToken(
                user.getUserId(), user.getUsername(), user.getRoleName());

        return new AuthResponse(
                newAccessToken,
                request.getRefreshToken(),
                "Bearer",
                900L,
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getRoleName()
        );
    }

    @Override
    @Transactional
    public void logout(String refreshTokenValue) {
        refreshTokenRepository.findByToken(refreshTokenValue)
                .ifPresent(token -> {
                    token.setRevoked(true);
                    refreshTokenRepository.save(token);
                });
    }

    private String generateAndSaveRefreshToken(User user) {
        refreshTokenRepository.revokeAllUserTokens(user);
        String tokenValue = UUID.randomUUID().toString();
        RefreshToken token = RefreshToken.builder()
                .user(user)
                .token(tokenValue)
                .expiresAt(LocalDateTime.now()
                        .plusSeconds(refreshExpiration / 1000))
                .revoked(false)
                .build();
        refreshTokenRepository.save(token);
        return tokenValue;
    }

    private void saveLoginHistory(User user, String identifier,
                                  String ip, String userAgent,
                                  String status) {
        LoginHistory history = LoginHistory.builder()
                .user(user)
                .loginIdentifier(identifier)
                .loginMethod("USERNAME_PASSWORD")
                .ipAddress(ip)
                .userAgent(userAgent)
                .loginStatus(status)
                .loginTime(LocalDateTime.now())
                .build();
        loginHistoryRepository.save(history);
    }
}