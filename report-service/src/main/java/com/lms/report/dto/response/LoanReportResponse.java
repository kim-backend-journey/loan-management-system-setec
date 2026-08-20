package com.lms.report.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LoanReportResponse(
        Long loanId,
        String loanNumber,
        String customerName,
        BigDecimal approvedAmount,
        BigDecimal outstandingBalance,
        String loanStatus,
        LocalDate startDate,
        LocalDate endDate
) {}