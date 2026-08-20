package com.lms.cron.repository;

import com.lms.cron.domain.LoanPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface LoanPaymentRepository extends JpaRepository<LoanPayment, Integer> {

    List<LoanPayment> findByPaymentStatusAndDueDateBefore(String paymentStatus, LocalDate date);

    List<LoanPayment> findByPaymentStatusAndDueDate(String paymentStatus, LocalDate date);
}