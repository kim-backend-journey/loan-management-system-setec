package com.lms.operation.service;

import com.lms.operation.dto.request.CreateNotificationRequest;
import com.lms.operation.dto.response.NotificationResponse;

import java.util.List;

public interface NotificationService {
    List<NotificationResponse> getAllNotifications();
    List<NotificationResponse> getByCustomer(Integer customerId);
    NotificationResponse createNotification(CreateNotificationRequest request);
    NotificationResponse markAsRead(Integer notificationId);
}