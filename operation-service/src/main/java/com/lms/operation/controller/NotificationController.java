package com.lms.operation.controller;

import com.lms.common.response.ApiResponse;
import com.lms.operation.dto.request.CreateNotificationRequest;
import com.lms.operation.dto.response.NotificationResponse;
import com.lms.operation.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @PreAuthorize("hasAnyRole('OFFICER','ADMIN')")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getAll() {
        return ResponseEntity.ok(
                ApiResponse.success(notificationService.getAllNotifications(),
                        "Notifications retrieved"));
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('OFFICER','ADMIN')")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getByCustomer(
            @PathVariable Integer customerId) {
        return ResponseEntity.ok(
                ApiResponse.success(notificationService.getByCustomer(customerId),
                        "Notifications retrieved"));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<NotificationResponse>> create(
            @RequestBody CreateNotificationRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        notificationService.createNotification(request),
                        "Notification created"));
    }

    @PatchMapping("/{id}/read")
    @PreAuthorize("hasAnyRole('OFFICER','ADMIN')")
    public ResponseEntity<ApiResponse<NotificationResponse>> markAsRead(
            @PathVariable Integer id) {
        return ResponseEntity.ok(
                ApiResponse.success(notificationService.markAsRead(id),
                        "Notification marked as read"));
    }
}