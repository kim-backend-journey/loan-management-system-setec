package com.lms.operation.repository;

import com.lms.operation.domain.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Integer> {
    List<PaymentTransaction> findByPayment_PaymentId(Integer paymentId);

    boolean existsByBankReference(String bankReference);

    boolean existsByIdempotencyKey(String idempotencyKey);

    Optional<PaymentTransaction> findByIdempotencyKey(String idempotencyKey);
}