package com.lms.operation.service;

import com.lms.operation.domain.Customer;
import com.lms.operation.domain.Notification;
import com.lms.operation.dto.request.CreateNotificationRequest;
import com.lms.operation.dto.response.NotificationResponse;
import com.lms.operation.exception.AppException;
import com.lms.operation.exception.ErrorCode;
import com.lms.operation.repository.CustomerRepository;
import com.lms.operation.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final CustomerRepository customerRepository;

    @Override
    public List<NotificationResponse> getAllNotifications() {
        return notificationRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<NotificationResponse> getByCustomer(Integer customerId) {
        return notificationRepository.findByCustomer_CustomerId(customerId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public NotificationResponse createNotification(CreateNotificationRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new AppException(ErrorCode.CUSTOMER_NOT_FOUND));

        LocalDateTime now = LocalDateTime.now();

        Notification notification = new Notification();
        notification.setCustomer(customer);
        notification.setCreatedBy(getCurrentUserId());
        notification.setNotificationType(request.getNotificationType());
        notification.setTitle(request.getTitle());
        notification.setMessage(request.getMessage());
        notification.setNotificationStatus("Sent");
        notification.setChannel(request.getChannel() != null ? request.getChannel() : "InApp");
        notification.setSentAt(now);
        notification.setIsRead(false);

        return toResponse(notificationRepository.save(notification));
    }

    @Override
    @Transactional
    public NotificationResponse markAsRead(Integer notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));

        notification.setIsRead(true);
        notification.setReadAt(LocalDateTime.now());

        return toResponse(notificationRepository.save(notification));
    }

    private NotificationResponse toResponse(Notification n) {
        return new NotificationResponse(
                n.getNotificationId(),
                n.getCustomer().getCustomerId(),
                n.getCreatedBy(),
                n.getNotificationType(),
                n.getTitle(),
                n.getMessage(),
                n.getNotificationStatus(),
                n.getChannel(),
                n.getSentAt(),
                n.getIsRead(),
                n.getReadAt(),
                n.getCreatedAt(),
                n.getUpdatedAt()
        );
    }

    private Integer getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof UsernamePasswordAuthenticationToken token) {
            Object details = token.getDetails();
            if (details instanceof Integer userId) {
                return userId;
            }
        }
        return 3;
    }
}