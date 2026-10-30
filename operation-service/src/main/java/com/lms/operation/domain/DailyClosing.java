package com.lms.operation.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "daily_closings", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"employee_id", "business_date"})
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DailyClosing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "closing_id")
    private Integer closingId;

    @Column(name = "employee_id", nullable = false)
    private Integer employeeId;

    @Column(name = "branch_id")
    private Integer branchId;

    @Column(name = "business_date", nullable = false)
    private LocalDate businessDate;

    @Column(name = "currency", length = 10)
    private String currency;

    @Column(name = "transaction_count")
    private Integer transactionCount;

    // cash the ledger says was collected
    @Column(name = "cash_expected", precision = 15, scale = 2)
    private BigDecimal cashExpected;

    // cash physically counted by the officer
    @Column(name = "cash_counted", precision = 15, scale = 2)
    private BigDecimal cashCounted;

    // counted - expected (negative = short, positive = over)
    @Column(name = "cash_variance", precision = 15, scale = 2)
    private BigDecimal cashVariance;

    @Column(name = "bank_transfer_total", precision = 15, scale = 2)
    private BigDecimal bankTransferTotal;

    @Column(name = "bank_pending_verification", precision = 15, scale = 2)
    private BigDecimal bankPendingVerification;

    @Column(name = "grand_total", precision = 15, scale = 2)
    private BigDecimal grandTotal;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "closed_by")
    private Integer closedBy;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

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
