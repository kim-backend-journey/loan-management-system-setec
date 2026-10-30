package com.lms.operation.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record DailyClosingResponse(
        Integer closingId,
        EmployeeSummaryResponse employee,
        Integer branchId,
        LocalDate businessDate,
        String currency,
        Integer transactionCount,
        BigDecimal cashExpected,
        BigDecimal cashCounted,
        BigDecimal cashVariance,
        BigDecimal bankTransferTotal,
        BigDecimal bankPendingVerification,
        BigDecimal grandTotal,
        String status,
        String notes,
        Integer closedBy,
        LocalDateTime closedAt
) {
}
