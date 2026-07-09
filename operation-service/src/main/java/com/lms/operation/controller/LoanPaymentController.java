package com.lms.operation.controller;

import com.lms.common.response.ApiResponse;
import com.lms.operation.dto.request.RecordPaymentRequest;
import com.lms.operation.dto.response.LoanPaymentResponse;
import com.lms.operation.service.LoanPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/loans")
@RequiredArgsConstructor
public class LoanPaymentController {

    private final LoanPaymentService loanPaymentService;

    @GetMapping("/{id}/payments")
    @PreAuthorize("hasAnyRole('OFFICER','ADMIN')")
    public ResponseEntity<ApiResponse<List<LoanPaymentResponse>>> getPayments(
            @PathVariable Integer id) {
        return ResponseEntity.ok(
                ApiResponse.success(loanPaymentService.getPayments(id),
                        "Payments retrieved"));
    }

    @GetMapping("/{id}/payments/{pid}")
    @PreAuthorize("hasAnyRole('OFFICER','ADMIN')")
    public ResponseEntity<ApiResponse<LoanPaymentResponse>> getPayment(
            @PathVariable Integer id,
            @PathVariable Integer pid) {
        return ResponseEntity.ok(
                ApiResponse.success(loanPaymentService.getPayment(id, pid),
                        "Payment retrieved"));
    }

    @PostMapping("/{id}/payments/{pid}/pay")
    @PreAuthorize("hasAnyRole('OFFICER','ADMIN')")
    public ResponseEntity<ApiResponse<LoanPaymentResponse>> pay(
            @PathVariable Integer id,
            @PathVariable Integer pid,
            @RequestBody RecordPaymentRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        loanPaymentService.recordPayment(id, pid, request),
                        "Payment recorded successfully"));
    }
}