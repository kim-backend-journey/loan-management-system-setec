package com.lms.operation.controller;

import com.lms.common.response.ApiResponse;
import com.lms.operation.dto.request.ApplicationStatusRequest;
import com.lms.operation.dto.request.CreateApplicationRequest;
import com.lms.operation.dto.request.UpdateApplicationRequest;
import com.lms.operation.dto.response.ApplicationResponse;
import com.lms.operation.dto.response.ApplicationStatusHistoryResponse;
import com.lms.operation.service.LoanApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
public class LoanApplicationController {

    private final LoanApplicationService loanApplicationService;

    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER','OFFICER','ADMIN')")
    public ResponseEntity<ApiResponse<ApplicationResponse>> createApplication(
            @Valid @RequestBody CreateApplicationRequest request) {

        ApplicationResponse response = loanApplicationService.createApplication(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Loan application created successfully"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('OFFICER','ADMIN')")
    public ResponseEntity<ApiResponse<List<ApplicationResponse>>> getAllApplications() {

        List<ApplicationResponse> response = loanApplicationService.getAllApplications();

        return ResponseEntity
                .ok(ApiResponse.success(response, "Loan applications retrieved successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('OFFICER','ADMIN')")
    public ResponseEntity<ApiResponse<ApplicationResponse>> getApplicationById(
            @PathVariable Integer id) {

        ApplicationResponse response = loanApplicationService.getApplicationById(id);

        return ResponseEntity
                .ok(ApiResponse.success(response, "Loan application retrieved successfully"));
    }

    @PostMapping("/{id}/submit")
    @PreAuthorize("hasAnyRole('OFFICER','ADMIN')")
    public ResponseEntity<ApiResponse<ApplicationResponse>> submitApplication(
            @PathVariable Integer id) {

        ApplicationResponse response = loanApplicationService.submitApplication(id);

        return ResponseEntity
                .ok(ApiResponse.success(response, "Loan application submitted successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OFFICER','ADMIN')")
    public ResponseEntity<ApiResponse<ApplicationResponse>> updateApplication(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateApplicationRequest request) {

        ApplicationResponse response = loanApplicationService.updateApplication(id, request);

        return ResponseEntity
                .ok(ApiResponse.success(response, "Loan application updated successfully"));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('OFFICER','ADMIN')")
    public ResponseEntity<ApiResponse<ApplicationResponse>> updateApplicationStatus(
            @PathVariable Integer id,
            @Valid @RequestBody ApplicationStatusRequest request) {

        ApplicationResponse response = loanApplicationService.updateApplicationStatus(id, request);

        return ResponseEntity
                .ok(ApiResponse.success(response, "Application status updated successfully"));
    }

    @GetMapping("/{id}/history")
    @PreAuthorize("hasAnyRole('OFFICER','ADMIN')")
    public ResponseEntity<ApiResponse<List<ApplicationStatusHistoryResponse>>> getApplicationHistory(
            @PathVariable Integer id) {

        List<ApplicationStatusHistoryResponse> response = loanApplicationService.getApplicationHistory(id);

        return ResponseEntity
                .ok(ApiResponse.success(response, "Application status history retrieved successfully"));
    }
}