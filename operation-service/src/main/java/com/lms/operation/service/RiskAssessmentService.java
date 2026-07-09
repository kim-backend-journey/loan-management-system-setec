package com.lms.operation.service;

import com.lms.operation.dto.request.CreateRiskAssessmentRequest;
import com.lms.operation.dto.response.RiskAssessmentResponse;

public interface RiskAssessmentService {
    RiskAssessmentResponse createRiskAssessment(Integer applicationId, CreateRiskAssessmentRequest request);
    RiskAssessmentResponse getRiskAssessment(Integer applicationId);
}