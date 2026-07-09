package com.lms.operation.controller;

import com.lms.common.response.ApiResponse;
import com.lms.operation.dto.response.LoanResponse;
import com.lms.operation.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @PostMapping("/api/v1/applications/{id}/disburse")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<LoanResponse>> disburse(@PathVariable Integer id) {
        return ResponseEntity.ok(
                ApiResponse.success(loanService.disburse(id), "Loan disbursed successfully"));
    }

    @GetMapping("/api/v1/loans/{id}")
    @PreAuthorize("hasAnyRole('OFFICER','ADMIN')")
    public ResponseEntity<ApiResponse<LoanResponse>> get(@PathVariable Integer id) {
        return ResponseEntity.ok(
                ApiResponse.success(loanService.getLoan(id), "Loan retrieved"));
    }
}