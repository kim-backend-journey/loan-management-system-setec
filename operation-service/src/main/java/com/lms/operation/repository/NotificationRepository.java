package com.lms.operation.repository;

import com.lms.operation.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByCustomer_CustomerId(Integer customerId);
    List<Notification> findByIsRead(Boolean isRead);
}