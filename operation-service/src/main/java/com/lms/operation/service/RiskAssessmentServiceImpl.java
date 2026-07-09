package com.lms.operation.service;

import com.lms.operation.domain.LoanApplication;
import com.lms.operation.domain.RiskAssessment;
import com.lms.operation.dto.request.CreateRiskAssessmentRequest;
import com.lms.operation.dto.response.RiskAssessmentResponse;
import com.lms.operation.exception.AppException;
import com.lms.operation.exception.ErrorCode;
import com.lms.operation.repository.LoanApplicationRepository;
import com.lms.operation.repository.RiskAssessmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RiskAssessmentServiceImpl implements RiskAssessmentService {

    private final RiskAssessmentRepository riskAssessmentRepository;
    private final LoanApplicationRepository loanApplicationRepository;

    @Override
    public RiskAssessmentResponse createRiskAssessment(Integer applicationId, CreateRiskAssessmentRequest request) {
        LoanApplication application = loanApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new AppException(ErrorCode.APPLICATION_NOT_FOUND));

        LocalDateTime now = LocalDateTime.now();

        RiskAssessment assessment = new RiskAssessment();
        assessment.setApplication(application);
        assessment.setRiskScore(request.getRiskScore());
        assessment.setRiskLevel(request.getRiskLevel());
        assessment.setAssessmentResult(request.getAssessmentResult());
        assessment.setAssessmentComment(request.getAssessmentComment());
        assessment.setAssessedBy(getCurrentUserId());
        assessment.setAssessedAt(now);

        return toResponse(riskAssessmentRepository.save(assessment));
    }

    @Override
    public RiskAssessmentResponse getRiskAssessment(Integer applicationId) {
        RiskAssessment assessment = riskAssessmentRepository.findByApplication_ApplicationId(applicationId)
                .orElseThrow(() -> new AppException(ErrorCode.RISK_ASSESSMENT_NOT_FOUND));
        return toResponse(assessment);
    }

    private RiskAssessmentResponse toResponse(RiskAssessment r) {
        return new RiskAssessmentResponse(
                r.getAssessmentId(),
                r.getApplication().getApplicationId(),
                r.getRiskScore(),
                r.getRiskLevel(),
                r.getAssessmentResult(),
                r.getAssessmentComment(),
                r.getAssessedBy(),
                r.getAssessedAt(),
                r.getCreatedAt(),
                r.getUpdatedAt()
        );
    }

    private Integer getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof UsernamePasswordAuthenticationToken token) {
            Object details = token.getDetails();
            if (details instanceof Integer userId) {
                return userId;
            }
        }
        return 3; // fallback
    }
}