package com.lms.operation.controller;

import com.lms.common.response.ApiResponse;
import com.lms.operation.dto.response.LoanScheduleDetailResponse;
import com.lms.operation.dto.response.PaginatedDueInstallmentResponse;
import com.lms.operation.dto.response.PaginatedLoanScheduleResponse;
import com.lms.operation.service.LoanScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/api/v1/loan-schedules")
@RequiredArgsConstructor
public class LoanScheduleController {

    private final LoanScheduleService loanScheduleService;

    @GetMapping
    @PreAuthorize("hasAnyRole('OFFICER','ADMIN')")
    public ResponseEntity<PaginatedLoanScheduleResponse> getLoanSchedules(
            @RequestParam(required = false) String status,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) Integer employeeId,
            @RequestParam(required = false) Integer branchId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {

        PaginatedLoanScheduleResponse response = loanScheduleService.getLoanSchedules(
                status, fromDate, toDate, employeeId, branchId, search, page, size);

        return ResponseEntity.ok(response);
    }

    // Same raw page shape as the list above: { items, page, size, totalItems, totalPages }.
    @GetMapping("/due-today")
    @PreAuthorize("hasAnyRole('OFFICER','ADMIN')")
    public ResponseEntity<PaginatedDueInstallmentResponse> getDueToday(
            @RequestParam(required = false) Integer employeeId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {

        return ResponseEntity.ok(loanScheduleService.getDueToday(employeeId, page, size));
    }

    @GetMapping("/overdue")
    @PreAuthorize("hasAnyRole('OFFICER','ADMIN')")
    public ResponseEntity<PaginatedDueInstallmentResponse> getOverdue(
            @RequestParam(required = false) Integer employeeId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {

        return ResponseEntity.ok(loanScheduleService.getOverdue(employeeId, page, size));
    }

    @GetMapping("/{loanId}")
    @PreAuthorize("hasAnyRole('OFFICER','ADMIN')")
    public ResponseEntity<ApiResponse<LoanScheduleDetailResponse>> getLoanScheduleDetail(
            @PathVariable Integer loanId) {

        LoanScheduleDetailResponse response =
                loanScheduleService.getLoanScheduleDetail(loanId);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Loan schedule retrieved"));
    }
}
