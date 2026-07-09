package com.lms.operation.dto.response;

import java.time.LocalDateTime;

public record NotificationResponse(
        Integer notificationId,
        Integer customerId,
        Integer createdBy,
        String notificationType,
        String title,
        String message,
        String notificationStatus,
        String channel,
        LocalDateTime sentAt,
        Boolean isRead,
        LocalDateTime readAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}