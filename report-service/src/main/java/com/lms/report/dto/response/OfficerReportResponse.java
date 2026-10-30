package com.lms.report.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Loan officer (CO) performance for one week, month or year. Disbursed and collected figures cover the
 * period; active loans, outstanding balance and overdue figures are the portfolio as it stands today.
 */
public record OfficerReportResponse(
        String period,
        LocalDate fromDate,
        LocalDate toDate,
        List<Row> officers
) {
    public record Row(
            Integer employeeId,
            String employeeCode,
            String officerName,
            Integer branchId,
            long loansDisbursed,
            BigDecimal amountDisbursed,
            long paymentsCount,
            BigDecimal totalCollected,
            BigDecimal principalCollected,
            BigDecimal interestCollected,
            BigDecimal penaltyCollected,
            long activeLoans,
            BigDecimal outstandingBalance,
            long overdueInstallments,
            BigDecimal overdueAmount
    ) {}
}
