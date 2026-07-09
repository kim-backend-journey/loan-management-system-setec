package com.lms.operation.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "loan_applications", indexes = {
        @Index(name = "idx_applications_customer", columnList = "customer_id"),
        @Index(name = "idx_applications_status",   columnList = "application_status"),
        @Index(name = "idx_applications_product",  columnList = "loan_product_id")
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class LoanApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "application_id")
    private Integer applicationId;

    @Column(name = "application_number", unique = true, nullable = false, length = 50)
    private String applicationNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_product_id", nullable = false)
    private LoanProduct loanProduct;

    @Column(name = "requested_amount", precision = 15, scale = 2)
    private BigDecimal requestedAmount;

    @Column(name = "requested_term_months")
    private Integer requestedTermMonths;

    @Column(name = "loan_purpose", columnDefinition = "TEXT")
    private String loanPurpose;

    @Column(name = "application_status", length = 30)
    @Builder.Default
    private String applicationStatus = "Draft";

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

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