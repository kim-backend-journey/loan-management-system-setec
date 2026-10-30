package com.lms.operation.controller;

import com.lms.common.response.ApiResponse;
import com.lms.operation.dto.response.EmployeeResponse;
import com.lms.operation.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    @PreAuthorize("hasAnyRole('OFFICER','ADMIN')")
    public ResponseEntity<ApiResponse<List<EmployeeResponse>>> getEmployees() {
        return ResponseEntity.ok(
                ApiResponse.success(employeeService.getEmployees(), "Employees retrieved"));
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('OFFICER','ADMIN')")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getCurrentEmployee() {
        return ResponseEntity.ok(
                ApiResponse.success(employeeService.getCurrentEmployee(), "Employee retrieved"));
    }
}
