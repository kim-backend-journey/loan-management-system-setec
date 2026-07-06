package com.lms.auth.dto.response;

public record RoleResponse(
        Integer roleId,
        String roleName,
        String description
) {}