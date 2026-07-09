package com.lms.operation.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RiskAssessmentResponse(
        Integer assessmentId,
        Integer applicationId,
        BigDecimal riskScore,
        String riskLevel,
        String assessmentResult,
        String assessmentComment,
        Integer assessedBy,
        LocalDateTime assessedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}