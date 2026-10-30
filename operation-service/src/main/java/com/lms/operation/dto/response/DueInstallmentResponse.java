package com.lms.operation.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

/** One unpaid installment in the "due today" or "overdue" list, with what is still owed on it. */
public record DueInstallmentResponse(
        Integer scheduleId,
        Integer paymentNo,
        LocalDate dueDate,
        long daysOverdue,
        BigDecimal amountDue,
        BigDecimal amountPaid,
        BigDecimal penaltyAmount,
        BigDecimal lateFee,
        BigDecimal outstandingAmount,
        String paymentStatus,
        Integer loanId,
        String loanNumber,
        String currency,
        Integer customerId,
        String customerName,
        String customerPhone,
        Integer employeeId,
        String employeeName
) {
}
