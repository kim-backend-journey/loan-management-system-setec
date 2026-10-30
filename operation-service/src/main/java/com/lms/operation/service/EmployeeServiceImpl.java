package com.lms.operation.service;

import com.lms.operation.domain.Employee;
import com.lms.operation.dto.response.EmployeeResponse;
import com.lms.operation.exception.AppException;
import com.lms.operation.exception.ErrorCode;
import com.lms.operation.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional
    public EmployeeResponse getCurrentEmployee() {
        Integer userId = getCurrentUserId();
        if (userId == null) {
            throw new AppException(ErrorCode.EMPLOYEE_NOT_FOUND);
        }

        employeeRepository.provisionOfficers();
        Employee employee = employeeRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_FOUND,
                        "No employee record is linked to this user"));

        return toResponse(employee);
    }

    @Override
    @Transactional
    public List<EmployeeResponse> getEmployees() {
        employeeRepository.provisionOfficers();
        return employeeRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    private EmployeeResponse toResponse(Employee employee) {
        return new EmployeeResponse(
                employee.getEmployeeId(),
                employee.getUserId(),
                employee.getBranchId(),
                employee.getEmployeeCode(),
                employee.getPosition(),
                employee.getStatus()
        );
    }

    private Integer getCurrentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof UsernamePasswordAuthenticationToken token
                && token.getDetails() instanceof Integer userId) {
            return userId;
        }
        return null;
    }
}
