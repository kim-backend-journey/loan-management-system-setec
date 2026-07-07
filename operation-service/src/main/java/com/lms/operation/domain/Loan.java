package com.lms.operation.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "loans", indexes = {
        @Index(name = "idx_loans_number", columnList = "loan_number"),
        @Index(name = "idx_loans_status", columnList = "loan_status")
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "loan_id")
    private Integer loanId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", unique = true, nullable = false)
    private LoanApplication application;

    @Column(name = "loan_number", unique = true, nullable = false, length = 50)
    private String loanNumber;

    @Column(name = "approved_amount", precision = 15, scale = 2)
    private BigDecimal approvedAmount;

    @Column(name = "interest_rate", precision = 5, scale = 2)
    private BigDecimal interestRate;

    @Column(name = "loan_term_months")
    private Integer loanTermMonths;

    @Column(name = "monthly_payment", precision = 15, scale = 2)
    private BigDecimal monthlyPayment;

    @Column(name = "outstanding_balance", precision = 15, scale = 2)
    private BigDecimal outstandingBalance;

    @Column(name = "loan_status", length = 20)
    @Builder.Default
    private String loanStatus = "Active";

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

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