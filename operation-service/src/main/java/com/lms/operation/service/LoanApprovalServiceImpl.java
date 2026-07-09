package com.lms.operation.service;

import com.lms.operation.domain.LoanApplication;
import com.lms.operation.domain.LoanApproval;
import com.lms.operation.dto.request.LoanApprovalRequest;
import com.lms.operation.dto.response.LoanApprovalResponse;
import com.lms.operation.exception.AppException;
import com.lms.operation.exception.ErrorCode;
import com.lms.operation.repository.LoanApplicationRepository;
import com.lms.operation.repository.LoanApprovalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LoanApprovalServiceImpl implements LoanApprovalService {

    private final LoanApprovalRepository loanApprovalRepository;
    private final LoanApplicationRepository loanApplicationRepository;

    @Override
    @Transactional
    public LoanApprovalResponse createApproval(Integer applicationId, LoanApprovalRequest request) {
        LoanApplication application = loanApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new AppException(ErrorCode.APPLICATION_NOT_FOUND));

        LocalDateTime now = LocalDateTime.now();

        LoanApproval approval = new LoanApproval();
        approval.setApplication(application);
        approval.setApprovedBy(getCurrentUserId());
        approval.setApprovalStatus(request.getApprovalStatus());
        approval.setDecisionReason(request.getDecisionReason());
        approval.setApprovedAt(now);
        approval = loanApprovalRepository.save(approval);

        if ("Approved".equalsIgnoreCase(request.getApprovalStatus())) {
            application.setApplicationStatus("Approved");
        } else if ("Rejected".equalsIgnoreCase(request.getApprovalStatus())) {
            application.setApplicationStatus("Rejected");
        }
        loanApplicationRepository.save(application);

        return toResponse(approval);
    }

    private LoanApprovalResponse toResponse(LoanApproval a) {
        return new LoanApprovalResponse(
                a.getApprovalId(),
                a.getApplication().getApplicationId(),
                a.getApprovedBy(),
                a.getApprovalStatus(),
                a.getDecisionReason(),
                a.getApprovedAt(),
                a.getCreatedAt(),
                a.getUpdatedAt()
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