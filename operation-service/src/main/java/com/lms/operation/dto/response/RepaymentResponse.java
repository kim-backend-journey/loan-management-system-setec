package com.lms.operation.dto.response;

import java.math.BigDecimal;

public record RepaymentResponse(
        Integer paymentId,
        String receiptNumber,
        String paymentMethod,
        String bankReference,
        BigDecimal totalAmount,
        AllocationResponse allocation,
        String scheduleStatus,
        BigDecimal loanRemainingPrincipal,
        String bankVerificationStatus,
        String currency
) {
}
