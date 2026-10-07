package com.lms.operation.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "disbursement_transactions", indexes = {
        @Index(name = "idx_disbursements_loan",         columnList = "loan_id"),
        @Index(name = "idx_disbursements_loan_request", columnList = "loan_request_id"),
        @Index(name = "idx_disbursements_bank_ref",     columnList = "bank_reference")
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DisbursementTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "disbursement_id")
    private Integer disbursementId;

    @Column(name = "loan_id")
    private Integer loanId;

    @Column(name = "loan_request_id")
    private Integer loanRequestId;

    @Column(name = "disbursement_date")
    private LocalDate disbursementDate;

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Column(name = "bank_reference", unique = true, length = 100)
    private String bankReference;

    @Column(name = "bank_account_id")
    private Integer bankAccountId;

    @Column(name = "amount", precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", length = 10)
    @Builder.Default
    private String currency = "KHR";

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "disbursed_by")
    private Integer disbursedBy;

    @Column(name = "status", length = 20)
    @Builder.Default
    private String status = "Completed";

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
