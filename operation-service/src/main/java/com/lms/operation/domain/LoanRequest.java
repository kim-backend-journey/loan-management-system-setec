package com.lms.operation.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "loan_requests", indexes = {
        @Index(name = "idx_loan_requests_customer", columnList = "customer_id"),
        @Index(name = "idx_loan_requests_status",   columnList = "status"),
        @Index(name = "idx_loan_requests_product",  columnList = "loan_product_id")
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class LoanRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "loan_request_id")
    private Integer loanRequestId;

    @Column(name = "loan_request_number", unique = true, nullable = false, length = 50)
    private String loanRequestNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_product_id", nullable = false)
    private LoanProduct loanProduct;

    @Column(name = "employee_id")
    private Integer employeeId;

    @Column(name = "branch_id")
    private Integer branchId;

    @Column(name = "principal_amount", precision = 15, scale = 2)
    private BigDecimal principalAmount;

    @Column(name = "currency", length = 10)
    @Builder.Default
    private String currency = "USD";

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "duration_unit", length = 20)
    @Builder.Default
    private String durationUnit = "MONTH";

    @Column(name = "repayment_frequency", length = 20)
    @Builder.Default
    private String repaymentFrequency = "MONTHLY";

    @Column(name = "monthly_payment_amount", precision = 15, scale = 2)
    private BigDecimal monthlyPaymentAmount;

    @Column(name = "service_fee_amount", precision = 15, scale = 2)
    private BigDecimal serviceFeeAmount;

    @Column(name = "purpose", columnDefinition = "TEXT")
    private String purpose;

    @Column(name = "requested_disbursement_date")
    private LocalDate requestedDisbursementDate;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "status", length = 30)
    @Builder.Default
    private String status = "PENDING";

    @Column(name = "reviewed_by")
    private Integer reviewedBy;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "review_comment", columnDefinition = "TEXT")
    private String reviewComment;

    @Column(name = "created_by")
    private Integer createdBy;

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
