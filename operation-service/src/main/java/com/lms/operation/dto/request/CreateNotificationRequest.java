package com.lms.operation.dto.request;

import lombok.Data;

@Data
public class CreateNotificationRequest {
    private Integer customerId;
    private String notificationType;
    private String title;
    private String message;
    private String channel;
}