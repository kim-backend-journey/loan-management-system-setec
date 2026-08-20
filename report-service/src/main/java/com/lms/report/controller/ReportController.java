package com.lms.report.controller;

import com.lms.common.response.ApiResponse;
import com.lms.report.dto.response.DashboardResponse;
import com.lms.report.dto.response.LoanReportResponse;
import com.lms.report.dto.response.PaymentReportResponse;
import com.lms.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "Reporting APIs for the LMS")
@SecurityRequirement(name = "Bearer Authentication")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER')")
    @Operation(summary = "Dashboard summary (counts + totals)")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard() {
        return ResponseEntity.ok(
                ApiResponse.success(reportService.getDashboard(), "Dashboard retrieved"));
    }

    @GetMapping("/loans")
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER')")
    @Operation(summary = "All loans with customer name")
    public ResponseEntity<ApiResponse<List<LoanReportResponse>>> getLoans() {
        return ResponseEntity.ok(
                ApiResponse.success(reportService.getLoanReports(), "Loan report retrieved"));
    }

    @GetMapping("/payments")
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER')")
    @Operation(summary = "All payments")
    public ResponseEntity<ApiResponse<List<PaymentReportResponse>>> getPayments() {
        return ResponseEntity.ok(
                ApiResponse.success(reportService.getPaymentReports(), "Payment report retrieved"));
    }
}