package com.lms.operation.service;

import com.lms.operation.dto.response.EmployeeResponse;

import java.util.List;

public interface EmployeeService {

    EmployeeResponse getCurrentEmployee();

    List<EmployeeResponse> getEmployees();
}
