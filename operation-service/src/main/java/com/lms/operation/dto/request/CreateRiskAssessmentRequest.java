package com.lms.operation.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateRiskAssessmentRequest {
    private BigDecimal riskScore;
    private String riskLevel;
    private String assessmentResult;
    private String assessmentComment;
}