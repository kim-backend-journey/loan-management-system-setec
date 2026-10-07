package com.lms.operation.controller;

import com.lms.common.response.ApiResponse;
import com.lms.operation.dto.request.CreateLoanRequestRequest;
import com.lms.operation.dto.response.LoanRequestResponse;
import com.lms.operation.service.LoanRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/loan-requests")
@RequiredArgsConstructor
public class LoanRequestController {

    private final LoanRequestService loanRequestService;

    @PostMapping
    @PreAuthorize("hasAnyRole('OFFICER','ADMIN')")
    public ResponseEntity<ApiResponse<LoanRequestResponse>> createLoanRequest(
            @Valid @RequestBody CreateLoanRequestRequest request) {

        LoanRequestResponse response = loanRequestService.createLoanRequest(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Loan request created successfully"));
    }
}
