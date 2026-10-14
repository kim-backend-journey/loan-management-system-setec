package com.lms.operation.controller;

import com.lms.common.response.ApiResponse;
import com.lms.operation.dto.request.RepaymentRequest;
import com.lms.operation.dto.response.RepaymentResponse;
import com.lms.operation.service.RepaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/repayments")
@RequiredArgsConstructor
public class RepaymentController {

    private final RepaymentService repaymentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('OFFICER','ADMIN')")
    public ResponseEntity<ApiResponse<RepaymentResponse>> createRepayment(
            @Valid @RequestBody RepaymentRequest request) {

        RepaymentService.RepaymentResult result = repaymentService.createRepayment(request);

        // idempotent replay -> 200 OK, new repayment -> 201 Created
        HttpStatus status = result.replay() ? HttpStatus.OK : HttpStatus.CREATED;
        String message = result.replay()
                ? "Repayment already processed (idempotent)"
                : "Repayment recorded successfully";

        return ResponseEntity
                .status(status)
                .body(ApiResponse.success(result.response(), message));
    }
}
