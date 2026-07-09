package com.lms.operation.controller;

import com.lms.common.response.ApiResponse;
import com.lms.operation.dto.request.CreateGuarantorRequest;
import com.lms.operation.dto.request.VerifyGuarantorRequest;
import com.lms.operation.dto.response.GuarantorResponse;
import com.lms.operation.service.GuarantorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/applications/{id}/guarantors")
@RequiredArgsConstructor
public class GuarantorController {

    private final GuarantorService guarantorService;

    @PostMapping
    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public ResponseEntity<ApiResponse<GuarantorResponse>> createGuarantor(
            @PathVariable("id") Integer applicationId,
            @Valid @RequestBody CreateGuarantorRequest request) {
        GuarantorResponse response = guarantorService.createGuarantor(applicationId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Guarantor created successfully"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<GuarantorResponse>>> getGuarantors(
            @PathVariable("id") Integer applicationId) {
        List<GuarantorResponse> response = guarantorService.getGuarantors(applicationId);
        return ResponseEntity.ok(ApiResponse.success(response, "Guarantors retrieved"));
    }

    @PatchMapping("/{gId}/verify")
    @PreAuthorize("hasAnyRole('OFFICER', 'ADMIN')")
    public ResponseEntity<ApiResponse<GuarantorResponse>> verifyGuarantor(
            @PathVariable("id") Integer applicationId,
            @PathVariable("gId") Integer guarantorId,
            @Valid @RequestBody VerifyGuarantorRequest request) {
        GuarantorResponse response = guarantorService.verifyGuarantor(applicationId, guarantorId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Guarantor verified"));
    }
}