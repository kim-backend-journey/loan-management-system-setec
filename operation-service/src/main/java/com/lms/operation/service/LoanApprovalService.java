package com.lms.operation.service;

import com.lms.operation.dto.request.LoanApprovalRequest;
import com.lms.operation.dto.response.LoanApprovalResponse;

public interface LoanApprovalService {
    LoanApprovalResponse createApproval(Integer applicationId, LoanApprovalRequest request);
}