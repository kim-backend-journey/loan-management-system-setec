package com.lms.operation.controller;

import com.lms.common.response.ApiResponse;
import com.lms.operation.dto.request.CloseDayRequest;
import com.lms.operation.dto.response.DailyClosingResponse;
import com.lms.operation.dto.response.DailyCollectionResponse;
import com.lms.operation.dto.response.DailyPaymentItemResponse;
import com.lms.operation.service.CollectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/collections")
@RequiredArgsConstructor
public class CollectionController {

    private final CollectionService collectionService;

    @GetMapping("/daily/employees/{employeeId}")
    @PreAuthorize("hasAnyRole('OFFICER','ADMIN')")
    public ResponseEntity<ApiResponse<DailyCollectionResponse>> getDailyEmployeeCollection(
            @PathVariable Integer employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        DailyCollectionResponse response =
                collectionService.getDailyEmployeeCollection(employeeId, date);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Daily collection retrieved"));
    }

    // One row per payment received on the date. Officers get their own; admins all, or one officer's.
    @GetMapping("/daily/payments")
    @PreAuthorize("hasAnyRole('OFFICER','ADMIN')")
    public ResponseEntity<ApiResponse<List<DailyPaymentItemResponse>>> getDailyPayments(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Integer employeeId) {

        return ResponseEntity.ok(ApiResponse.success(
                collectionService.getDailyPayments(date, employeeId), "Daily payments retrieved"));
    }

    @PostMapping("/daily/employees/{employeeId}/close")
    @PreAuthorize("hasAnyRole('OFFICER','ADMIN')")
    public ResponseEntity<ApiResponse<DailyClosingResponse>> closeDay(
            @PathVariable Integer employeeId,
            @Valid @RequestBody CloseDayRequest request) {

        DailyClosingResponse response = collectionService.closeDay(employeeId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Business day closed"));
    }

    // Corrects the banknote count of a closed day; same body as closing it
    @PutMapping("/daily/employees/{employeeId}/close")
    @PreAuthorize("hasAnyRole('OFFICER','ADMIN')")
    public ResponseEntity<ApiResponse<DailyClosingResponse>> updateDayClosing(
            @PathVariable Integer employeeId,
            @Valid @RequestBody CloseDayRequest request) {

        return ResponseEntity.ok(ApiResponse.success(
                collectionService.updateDayClosing(employeeId, request), "Day closing updated"));
    }

    @GetMapping("/daily/employees/{employeeId}/close")
    @PreAuthorize("hasAnyRole('OFFICER','ADMIN')")
    public ResponseEntity<ApiResponse<DailyClosingResponse>> getDayClosing(
            @PathVariable Integer employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        DailyClosingResponse response = collectionService.getDayClosing(employeeId, date);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Day closing retrieved"));
    }
}
