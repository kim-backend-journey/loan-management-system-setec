package com.lms.operation.controller;

import com.lms.common.response.ApiResponse;
import com.lms.operation.dto.request.LoanApprovalRequest;
import com.lms.operation.dto.response.LoanApprovalResponse;
import com.lms.operation.service.LoanApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/applications/{id}/approval")
@RequiredArgsConstructor
public class LoanApprovalController {

    private final LoanApprovalService loanApprovalService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<LoanApprovalResponse>> create(
            @PathVariable Integer id,
            @RequestBody LoanApprovalRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success(loanApprovalService.createApproval(id, request),
                        "Approval decision recorded successfully"));
    }
}