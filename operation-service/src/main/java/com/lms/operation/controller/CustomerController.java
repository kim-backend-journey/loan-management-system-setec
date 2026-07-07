package com.lms.operation.controller;

import com.lms.operation.dto.request.CreateCustomerRequest;
import com.lms.operation.dto.request.EmploymentRequest;
import com.lms.operation.dto.request.UpdateCustomerRequest;
import com.lms.operation.dto.response.CustomerResponse;
import com.lms.operation.dto.response.EmploymentResponse;
import com.lms.operation.service.CustomerService;
import com.lms.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','OFFICER')")
    public ResponseEntity<ApiResponse<CustomerResponse>> createCustomer(
            @Valid @RequestBody CreateCustomerRequest request) {
        CustomerResponse response = customerService.createCustomer(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Customer created"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','OFFICER')")
    public ResponseEntity<ApiResponse<List<CustomerResponse>>> getAllCustomers() {
        List<CustomerResponse> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(
                ApiResponse.success(customers, "Customers retrieved"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','OFFICER')")
    public ResponseEntity<ApiResponse<CustomerResponse>> getCustomerById(
            @PathVariable Integer id) {
        CustomerResponse response = customerService.getCustomerById(id);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Customer retrieved"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','OFFICER')")
    public ResponseEntity<ApiResponse<CustomerResponse>> updateCustomer(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateCustomerRequest request) {
        CustomerResponse response = customerService.updateCustomer(id, request);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Customer updated"));
    }

    @GetMapping("/{id}/employment")
    @PreAuthorize("hasAnyRole('ADMIN','OFFICER')")
    public ResponseEntity<ApiResponse<List<EmploymentResponse>>> getEmployment(
            @PathVariable Integer id) {
        List<EmploymentResponse> employment =
                customerService.getEmploymentHistory(id);
        return ResponseEntity.ok(
                ApiResponse.success(employment, "Employment history retrieved"));
    }

    @PostMapping("/{id}/employment")
    @PreAuthorize("hasAnyRole('ADMIN','OFFICER')")
    public ResponseEntity<ApiResponse<EmploymentResponse>> addEmployment(
            @PathVariable Integer id,
            @Valid @RequestBody EmploymentRequest request) {
        EmploymentResponse response =
                customerService.addEmployment(id, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Employment added"));
    }

    @PutMapping("/{id}/employment/{empId}")
    @PreAuthorize("hasAnyRole('ADMIN','OFFICER')")
    public ResponseEntity<ApiResponse<EmploymentResponse>> updateEmployment(
            @PathVariable Integer id,
            @PathVariable Integer empId,
            @Valid @RequestBody EmploymentRequest request) {
        EmploymentResponse response =
                customerService.updateEmployment(id, empId, request);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Employment updated"));
    }
}