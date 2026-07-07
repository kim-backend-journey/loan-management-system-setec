package com.lms.operation.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "risk_assessments")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class RiskAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assessment_id")
    private Integer assessmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private LoanApplication application;

    @Column(name = "risk_score", precision = 5, scale = 2)
    private BigDecimal riskScore;

    @Column(name = "risk_level", length = 20)
    private String riskLevel;
    // Low / Medium / High

    @Column(name = "assessment_result", length = 30)
    private String assessmentResult;
    // Recommended / Manual Review / High Risk

    @Column(name = "assessment_comment", columnDefinition = "TEXT")
    private String assessmentComment;

    @Column(name = "assessed_by")
    private Integer assessedBy;

    @Column(name = "assessed_at")
    private LocalDateTime assessedAt;

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