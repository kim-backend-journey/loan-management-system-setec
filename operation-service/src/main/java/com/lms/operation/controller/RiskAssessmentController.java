package com.lms.operation.controller;

import com.lms.common.response.ApiResponse;
import com.lms.operation.dto.request.CreateRiskAssessmentRequest;
import com.lms.operation.dto.response.RiskAssessmentResponse;
import com.lms.operation.service.RiskAssessmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/applications/{id}/risk-assessment")
@RequiredArgsConstructor
public class RiskAssessmentController {

    private final RiskAssessmentService riskAssessmentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('OFFICER','ADMIN')")
    public ResponseEntity<ApiResponse<RiskAssessmentResponse>> create(
            @PathVariable Integer id,
            @RequestBody CreateRiskAssessmentRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success(riskAssessmentService.createRiskAssessment(id, request),
                        "Risk assessment created successfully"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('OFFICER','ADMIN')")
    public ResponseEntity<ApiResponse<RiskAssessmentResponse>> get(@PathVariable Integer id) {
        return ResponseEntity.ok(
                ApiResponse.success(riskAssessmentService.getRiskAssessment(id),
                        "Risk assessment retrieved"));
    }
}