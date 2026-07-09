package com.lms.operation.repository;

import com.lms.operation.domain.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Integer> {
    List<PaymentTransaction> findByPayment_PaymentId(Integer paymentId);
}