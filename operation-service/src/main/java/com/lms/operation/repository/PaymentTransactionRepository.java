package com.lms.operation.repository;

import com.lms.operation.domain.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Integer> {
    List<PaymentTransaction> findByPayment_PaymentId(Integer paymentId);

    boolean existsByBankReference(String bankReference);

    boolean existsByIdempotencyKey(String idempotencyKey);

    Optional<PaymentTransaction> findByIdempotencyKey(String idempotencyKey);

    /** Non-voided payments received in [start, end), newest first; optionally only one receiver's. */
    @Query("""
            select t from PaymentTransaction t
              join fetch t.payment p
              join fetch p.loan l
            where t.transactionDate >= :start and t.transactionDate < :end
              and (t.isVoided is null or t.isVoided = false)
              and (:receivedBy is null or t.receivedBy = :receivedBy)
            order by t.transactionDate desc, t.transactionId desc
            """)
    List<PaymentTransaction> findReceivedBetween(@Param("start") LocalDateTime start,
                                                 @Param("end") LocalDateTime end,
                                                 @Param("receivedBy") Integer receivedBy);
}