package com.lms.operation.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_transactions", indexes = {
        @Index(name = "idx_transactions_ref", columnList = "reference_number")
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Integer transactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private LoanPayment payment;

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Column(name = "amount", precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "reference_number", length = 100)
    private String referenceNumber;

    @Column(name = "transaction_status", length = 20)
    @Builder.Default
    private String transactionStatus = "Pending";
    // Pending / Success / Failed

    @Column(name = "received_by", nullable = false)
    private Integer receivedBy;

    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;

    // --- enhanced repayment columns ---
    @Column(name = "bank_account_id")
    private Integer bankAccountId;

    @Column(name = "bank_reference", unique = true, length = 100)
    private String bankReference;

    @Column(name = "bank_verification_status", length = 20)
    private String bankVerificationStatus;

    @Column(name = "idempotency_key", unique = true, length = 100)
    private String idempotencyKey;

    @Column(name = "receipt_number", length = 50)
    private String receiptNumber;

    @Column(name = "currency", length = 10)
    private String currency;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "is_voided")
    @Builder.Default
    private Boolean isVoided = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}