package com.lms.auth.controller;

import com.lms.auth.dto.request.ChangePasswordRequest;
import com.lms.auth.dto.request.CreateUserRequest;
import com.lms.auth.dto.request.UpdateProfileRequest;
import com.lms.auth.dto.response.UserResponse;
import com.lms.auth.service.UserService;
import com.lms.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // ── My Profile ──────────────────────────────────────────

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMyProfile(
            @AuthenticationPrincipal UserDetails userDetails) {

        UserResponse response = userService
                .getMyProfile(userDetails.getUsername());
        return ResponseEntity.ok(
                ApiResponse.success(response, "Profile retrieved"));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateMyProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateProfileRequest request) {

        UserResponse response = userService
                .updateMyProfile(userDetails.getUsername(), request);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Profile updated"));
    }

    @PutMapping("/me/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ChangePasswordRequest request) {

        userService.changePassword(userDetails.getUsername(), request);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Password changed successfully"));
    }

    // ── Admin Only ───────────────────────────────────────────

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @Valid @RequestBody CreateUserRequest request) {

        UserResponse response = userService.createUser(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "User created successfully"));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {

        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(
                ApiResponse.success(users, "Users retrieved"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(
            @PathVariable Integer id) {

        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(
                ApiResponse.success(response, "User retrieved"));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserStatus(
            @PathVariable Integer id,
            @RequestParam String status) {

        UserResponse response = userService.updateUserStatus(id, status);
        return ResponseEntity.ok(
                ApiResponse.success(response, "User status updated"));
    }
}