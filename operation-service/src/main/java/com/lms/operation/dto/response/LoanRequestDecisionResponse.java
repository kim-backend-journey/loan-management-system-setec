package com.lms.operation.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LoanRequestDecisionResponse(
        Integer loanRequestId,
        String loanRequestNumber,
        String status,
        Integer reviewedBy,
        LocalDateTime reviewedAt,
        String comment,
        Integer customerId,
        String customerName,
        BigDecimal principalAmount,
        String currency
) {
}
