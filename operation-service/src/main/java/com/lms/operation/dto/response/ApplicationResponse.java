package com.lms.operation.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ApplicationResponse(
        Integer applicationId,
        String applicationNumber,
        Integer customerId,
        String customerName,
        Integer loanProductId,
        String productName,
        BigDecimal requestedAmount,
        Integer requestedTermMonths,
        String loanPurpose,
        String applicationStatus,
        LocalDateTime submittedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}