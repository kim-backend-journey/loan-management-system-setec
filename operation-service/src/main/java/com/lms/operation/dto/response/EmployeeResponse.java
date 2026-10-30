package com.lms.operation.dto.response;

public record EmployeeResponse(
        Integer employeeId,
        Integer userId,
        Integer branchId,
        String employeeCode,
        String position,
        String status
) {
}
