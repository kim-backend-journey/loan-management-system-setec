package com.lms.auth.service;

import com.lms.auth.domain.Role;
import com.lms.auth.dto.request.RoleRequest;
import com.lms.auth.dto.response.RoleResponse;
import com.lms.auth.exception.AppException;
import com.lms.auth.exception.ErrorCode;
import com.lms.auth.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public RoleResponse getRoleById(Integer roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
        return toResponse(role);
    }

    @Override
    @Transactional
    public RoleResponse createRole(RoleRequest request) {
        if (roleRepository.findByRoleName(
                request.getRoleName().toUpperCase()).isPresent()) {
            throw new AppException(ErrorCode.ROLE_ALREADY_EXISTS);
        }

        Role role = Role.builder()
                .roleName(request.getRoleName().toUpperCase())
                .description(request.getDescription())
                .build();

        roleRepository.save(role);
        log.info("Role created: {}", role.getRoleName());
        return toResponse(role);
    }

    @Override
    @Transactional
    public RoleResponse updateRole(Integer roleId, RoleRequest request) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        // Check name conflict only if name is changing
        if (!role.getRoleName().equals(request.getRoleName().toUpperCase())) {
            if (roleRepository.findByRoleName(
                    request.getRoleName().toUpperCase()).isPresent()) {
                throw new AppException(ErrorCode.ROLE_ALREADY_EXISTS);
            }
        }

        role.setRoleName(request.getRoleName().toUpperCase());
        role.setDescription(request.getDescription());
        roleRepository.save(role);

        log.info("Role updated: {}", role.getRoleName());
        return toResponse(role);
    }

    private RoleResponse toResponse(Role role) {
        return new RoleResponse(
                role.getRoleId(),
                role.getRoleName(),
                role.getDescription()
        );
    }
}