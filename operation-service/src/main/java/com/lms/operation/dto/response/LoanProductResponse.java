package com.lms.operation.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LoanProductResponse(
        Integer loanProductId,
        String productName,
        BigDecimal maxLoanAmount,
        BigDecimal minimumIncome,
        BigDecimal interestRate,
        Integer minimumTermMonths,
        Integer maximumTermMonths,
        BigDecimal processingFee,
        String description,
        String status,
        Integer reviewedBy,
        LocalDateTime reviewedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}