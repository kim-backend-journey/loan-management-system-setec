package com.lms.operation.dto.response;

import java.time.LocalDateTime;

public record LoanApprovalResponse(
        Integer approvalId,
        Integer applicationId,
        Integer approvedBy,
        String approvalStatus,
        String decisionReason,
        LocalDateTime approvedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}