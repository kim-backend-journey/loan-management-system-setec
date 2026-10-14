package com.lms.operation.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LoanScheduleItemResponse(
        Integer scheduleId,
        Integer loanId,
        String loanNumber,
        Integer customerId,
        String customerName,
        String customerPhone,
        Integer employeeId,
        String employeeName,
        BigDecimal principalAmount,
        String currency,
        Integer duration,
        String repaymentFrequency,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal installmentAmount,
        Integer totalSchedules,
        Integer paidSchedules,
        Integer remainingSchedules,
        BigDecimal totalPayableAmount,
        BigDecimal totalPaidAmount,
        BigDecimal totalRemainingAmount,
        double progressPercentage,
        String status
) {
}
