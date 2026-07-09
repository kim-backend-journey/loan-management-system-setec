package com.lms.operation.controller;

import com.lms.common.response.ApiResponse;
import com.lms.operation.dto.request.CreateCollateralRequest;
import com.lms.operation.dto.response.CollateralResponse;
import com.lms.operation.service.CollateralService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/applications/{id}/collaterals")
@RequiredArgsConstructor
public class CollateralController {

    private final CollateralService collateralService;

    @PostMapping
    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public ResponseEntity<ApiResponse<CollateralResponse>> createCollateral(
            @PathVariable("id") Integer applicationId,
            @Valid @RequestBody CreateCollateralRequest request) {
        CollateralResponse response = collateralService.createCollateral(applicationId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Collateral created successfully"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<CollateralResponse>>> getCollaterals(
            @PathVariable("id") Integer applicationId) {
        List<CollateralResponse> response = collateralService.getCollaterals(applicationId);
        return ResponseEntity.ok(ApiResponse.success(response, "Collaterals retrieved"));
    }
}