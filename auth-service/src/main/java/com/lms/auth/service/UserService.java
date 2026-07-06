package com.lms.auth.service;

import com.lms.auth.dto.request.ChangePasswordRequest;
import com.lms.auth.dto.request.CreateUserRequest;
import com.lms.auth.dto.request.UpdateProfileRequest;
import com.lms.auth.dto.response.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse getMyProfile(String username);

    UserResponse updateMyProfile(String username,
                                 UpdateProfileRequest request);

    void changePassword(String username,
                        ChangePasswordRequest request);

    UserResponse createUser(CreateUserRequest request);

    List<UserResponse> getAllUsers();

    UserResponse getUserById(Integer userId);

    UserResponse updateUserStatus(Integer userId, String status);
}