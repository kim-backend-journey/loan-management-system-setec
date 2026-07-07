package com.lms.operation.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "loan_products")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class LoanProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "loan_product_id")
    private Integer loanProductId;

    @Column(name = "product_name", nullable = false, length = 255)
    private String productName;

    @Column(name = "max_loan_amount", precision = 15, scale = 2)
    private BigDecimal maxLoanAmount;

    @Column(name = "minimum_income", precision = 15, scale = 2)
    private BigDecimal minimumIncome;

    @Column(name = "interest_rate", precision = 5, scale = 2)
    private BigDecimal interestRate;

    @Column(name = "minimum_term_months")
    private Integer minimumTermMonths;

    @Column(name = "maximum_term_months")
    private Integer maximumTermMonths;

    @Column(name = "processing_fee", precision = 15, scale = 2)
    private BigDecimal processingFee;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "status", length = 20)
    @Builder.Default
    private String status = "Active";

    @Column(name = "reviewed_by")
    private Integer reviewedBy;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

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