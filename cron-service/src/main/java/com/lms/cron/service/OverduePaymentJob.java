package com.lms.cron.service;

import com.lms.cron.domain.Customer;
import com.lms.cron.domain.LoanPayment;
import com.lms.cron.domain.Notification;
import com.lms.cron.repository.LoanPaymentRepository;
import com.lms.cron.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OverduePaymentJob {

    private final LoanPaymentRepository loanPaymentRepository;
    private final NotificationRepository notificationRepository;

    private static final String STATUS_PENDING = "Pending";
    private static final String STATUS_OVERDUE = "Overdue";
    private static final String CHANNEL_INAPP = "InApp";
    private static final Integer SYSTEM_USER = 1;

    /**
     * Job 1 — Mark Overdue Payments.
     * Runs daily at midnight.
     */
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void markOverduePayments() {
        LocalDate today = LocalDate.now();
        log.info("[OverdueJob] Scanning for pending payments due before {}", today);

        List<LoanPayment> overduePayments =
                loanPaymentRepository.findByPaymentStatusAndDueDateBefore(STATUS_PENDING, today);

        if (overduePayments.isEmpty()) {
            log.info("[OverdueJob] No overdue payments found.");
            return;
        }

        List<Notification> notifications = new ArrayList<>();
        for (LoanPayment payment : overduePayments) {
            payment.setPaymentStatus(STATUS_OVERDUE);

            Customer customer = resolveCustomer(payment);
            if (customer == null) {
                log.warn("[OverdueJob] Payment id={} has no resolvable customer; skipping notification.",
                        payment.getPaymentId());
                continue;
            }

            notifications.add(buildNotification(
                    customer,
                    "PAYMENT_OVERDUE",
                    "Payment Overdue",
                    "Your payment due on " + payment.getDueDate()
                            + " is now overdue. Please make the payment as soon as possible."
            ));
        }

        loanPaymentRepository.saveAll(overduePayments);
        notificationRepository.saveAll(notifications);

        log.info("[OverdueJob] Marked {} payment(s) overdue and created {} notification(s).",
                overduePayments.size(), notifications.size());
    }

    /**
     * Job 2 — Payment Reminders.
     * Runs daily at 8am.
     */
    @Scheduled(cron = "0 0 8 * * ?")
    @Transactional
    public void sendPaymentReminders() {
        LocalDate targetDate = LocalDate.now().plusDays(3);
        log.info("[ReminderJob] Scanning for pending payments due on {}", targetDate);

        List<LoanPayment> upcomingPayments =
                loanPaymentRepository.findByPaymentStatusAndDueDate(STATUS_PENDING, targetDate);

        if (upcomingPayments.isEmpty()) {
            log.info("[ReminderJob] No upcoming payments found.");
            return;
        }

        List<Notification> notifications = new ArrayList<>();
        for (LoanPayment payment : upcomingPayments) {
            Customer customer = resolveCustomer(payment);
            if (customer == null) {
                log.warn("[ReminderJob] Payment id={} has no resolvable customer; skipping.",
                        payment.getPaymentId());
                continue;
            }

            notifications.add(buildNotification(
                    customer,
                    "PAYMENT_REMINDER",
                    "Payment Reminder",
                    "Reminder: your payment of " + payment.getAmountDue()
                            + " is due on " + payment.getDueDate() + "."
            ));
        }

        notificationRepository.saveAll(notifications);
        log.info("[ReminderJob] Created {} reminder notification(s).", notifications.size());
    }

    private Customer resolveCustomer(LoanPayment payment) {
        if (payment.getLoan() == null) {
            return null;
        }
        if (payment.getLoan().getApplication() == null) {
            return null;
        }
        return payment.getLoan().getApplication().getCustomer();
    }

    private Notification buildNotification(Customer customer, String type, String title, String message) {
        Notification notification = new Notification();
        notification.setCustomer(customer);
        notification.setCreatedBy(SYSTEM_USER);
        notification.setNotificationType(type);
        notification.setChannel(CHANNEL_INAPP);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setNotificationStatus("Sent");
        notification.setIsRead(false);
        notification.setSentAt(LocalDateTime.now());
        return notification;
    }
}