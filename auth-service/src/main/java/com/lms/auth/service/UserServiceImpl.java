package com.lms.auth.service;

import com.lms.auth.domain.Role;
import com.lms.auth.domain.User;
import com.lms.auth.dto.request.ChangePasswordRequest;
import com.lms.auth.dto.request.CreateUserRequest;
import com.lms.auth.dto.request.UpdateProfileRequest;
import com.lms.auth.dto.response.UserResponse;
import com.lms.auth.exception.AppException;
import com.lms.auth.exception.ErrorCode;
import com.lms.auth.repository.RoleRepository;
import com.lms.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse getMyProfile(String username) {
        User user = findByUsername(username);
        return toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateMyProfile(String username,
                                        UpdateProfileRequest request) {
        User user = findByUsername(username);

        // Check email uniqueness if changing
        if (request.getEmail() != null &&
                !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
            }
            user.setEmail(request.getEmail());
        }

        // Check phone uniqueness if changing
        if (request.getPhoneNumber() != null &&
                !request.getPhoneNumber().equals(user.getPhoneNumber())) {
            if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
                throw new AppException(ErrorCode.PHONE_ALREADY_EXISTS);
            }
            user.setPhoneNumber(request.getPhoneNumber());
        }

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }

        userRepository.save(user);
        log.info("Profile updated for user: {}", username);
        return toResponse(user);
    }

    @Override
    @Transactional
    public void changePassword(String username,
                               ChangePasswordRequest request) {
        User user = findByUsername(username);

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(),
                user.getPassword())) {
            throw new AppException(ErrorCode.WRONG_PASSWORD);
        }

        // Verify new passwords match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new AppException(ErrorCode.PASSWORD_MISMATCH);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("Password changed for user: {}", username);
    }

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        // Check username
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_ALREADY_EXISTS);
        }

        // Check email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // Find role
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .role(role)
                .status("Active")
                .emailVerified(false)
                .phoneVerified(false)
                .build();

        userRepository.save(user);
        log.info("User created: {}", request.getUsername());
        return toResponse(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public UserResponse getUserById(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUserStatus(Integer userId, String status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        user.setStatus(status);
        userRepository.save(user);
        log.info("User {} status updated to: {}", userId, status);
        return toResponse(user);
    }

    private User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getFirstName(),
                user.getLastName(),
                user.getRoleName(),
                user.getStatus(),
                user.getEmailVerified(),
                user.getPhoneVerified(),
                user.getLastLoginAt(),
                user.getCreatedAt()
        );
    }
}