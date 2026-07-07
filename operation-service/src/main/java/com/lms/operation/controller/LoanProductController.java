package com.lms.operation.controller;

import com.lms.common.response.ApiResponse;
import com.lms.operation.dto.request.CreateLoanProductRequest;
import com.lms.operation.dto.request.LoanProductStatusRequest;
import com.lms.operation.dto.request.UpdateLoanProductRequest;
import com.lms.operation.dto.response.LoanProductResponse;
import com.lms.operation.service.LoanProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/loan-products")
@RequiredArgsConstructor
public class LoanProductController {

    private final LoanProductService loanProductService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<LoanProductResponse>> createLoanProduct(
            @Valid @RequestBody CreateLoanProductRequest request) {

        LoanProductResponse response = loanProductService.createLoanProduct(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Loan product created successfully"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','OFFICER')")
    public ResponseEntity<ApiResponse<List<LoanProductResponse>>> getAllLoanProducts() {

        List<LoanProductResponse> response = loanProductService.getAllLoanProducts();

        return ResponseEntity
                .ok(ApiResponse.success(response, "Loan products retrieved successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','OFFICER')")
    public ResponseEntity<ApiResponse<LoanProductResponse>> getLoanProductById(
            @PathVariable Integer id) {

        LoanProductResponse response = loanProductService.getLoanProductById(id);

        return ResponseEntity
                .ok(ApiResponse.success(response, "Loan product retrieved successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<LoanProductResponse>> updateLoanProduct(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateLoanProductRequest request) {

        LoanProductResponse response = loanProductService.updateLoanProduct(id, request);

        return ResponseEntity
                .ok(ApiResponse.success(response, "Loan product updated successfully"));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<LoanProductResponse>> updateLoanProductStatus(
            @PathVariable Integer id,
            @Valid @RequestBody LoanProductStatusRequest request) {

        LoanProductResponse response = loanProductService.updateLoanProductStatus(id, request);

        return ResponseEntity
                .ok(ApiResponse.success(response, "Loan product status updated successfully"));
    }
}