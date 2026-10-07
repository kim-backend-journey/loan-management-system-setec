package com.lms.operation.controller;

import com.lms.common.response.ApiResponse;
import com.lms.operation.dto.request.DisburseRequest;
import com.lms.operation.dto.response.DisburseResponse;
import com.lms.operation.service.DisburseService;
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
public class DisburseController {

    private final DisburseService disburseService;

    @PostMapping("/{loanRequestId}/disburse")
    @PreAuthorize("hasAnyRole('ADMIN','OFFICER')")
    public ResponseEntity<ApiResponse<DisburseResponse>> disburse(
            @PathVariable Integer loanRequestId,
            @Valid @RequestBody DisburseRequest request) {

        DisburseResponse response = disburseService.disburse(loanRequestId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Loan disbursed successfully"));
    }
}
