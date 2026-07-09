package com.lms.operation.dto.request;

import lombok.Data;

@Data
public class LoanApprovalRequest {
    private String approvalStatus;   // "Approved" / "Rejected"
    private String decisionReason;
}