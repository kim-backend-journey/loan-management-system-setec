package com.lms.operation.controller;

import com.lms.common.response.ApiResponse;
import com.lms.operation.dto.response.DailyCollectionResponse;
import com.lms.operation.service.CollectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

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
}
