package com.lms.auth.service;

import com.lms.auth.dto.request.RoleRequest;
import com.lms.auth.dto.response.RoleResponse;

import java.util.List;

public interface RoleService {

    List<RoleResponse> getAllRoles();

    RoleResponse getRoleById(Integer roleId);

    RoleResponse createRole(RoleRequest request);

    RoleResponse updateRole(Integer roleId, RoleRequest request);
}