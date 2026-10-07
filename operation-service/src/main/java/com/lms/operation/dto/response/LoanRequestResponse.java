package com.lms.operation.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record LoanRequestResponse(
        Integer loanRequestId,
        String loanRequestNumber,
        Integer customerId,
        String customerName,
        Integer loanProductId,
        String productName,
        Integer employeeId,
        Integer branchId,
        BigDecimal principalAmount,
        String currency,
        Integer duration,
        String durationUnit,
        String repaymentFrequency,
        BigDecimal monthlyPaymentAmount,
        BigDecimal serviceFeeAmount,
        String purpose,
        LocalDate requestedDisbursementDate,
        String notes,
        String status,
        Integer createdBy,
        LocalDateTime createdAt
) {
}
