package com.lms.operation.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record LoanResponse(
        Integer loanId,
        Integer applicationId,
        String loanNumber,
        BigDecimal approvedAmount,
        BigDecimal interestRate,
        Integer loanTermMonths,
        BigDecimal monthlyPayment,
        BigDecimal outstandingBalance,
        String loanStatus,
        LocalDate startDate,
        LocalDate endDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}