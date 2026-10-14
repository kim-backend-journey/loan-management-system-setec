package com.lms.operation.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "collection_ledger", indexes = {
        @Index(name = "idx_ledger_employee",     columnList = "employee_id"),
        @Index(name = "idx_ledger_business_date", columnList = "business_date"),
        @Index(name = "idx_ledger_transaction",  columnList = "transaction_id")
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CollectionLedger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ledger_id")
    private Integer ledgerId;

    @Column(name = "employee_id")
    private Integer employeeId;

    @Column(name = "branch_id")
    private Integer branchId;

    @Column(name = "business_date")
    private LocalDate businessDate;

    @Column(name = "payment_id")
    private Integer paymentId;

    @Column(name = "transaction_id")
    private Integer transactionId;

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Column(name = "principal_amount", precision = 15, scale = 2)
    private BigDecimal principalAmount;

    @Column(name = "interest_amount", precision = 15, scale = 2)
    private BigDecimal interestAmount;

    @Column(name = "service_fee_amount", precision = 15, scale = 2)
    private BigDecimal serviceFeeAmount;

    @Column(name = "penalty_amount", precision = 15, scale = 2)
    private BigDecimal penaltyAmount;

    @Column(name = "total_amount", precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "currency", length = 10)
    private String currency;

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
