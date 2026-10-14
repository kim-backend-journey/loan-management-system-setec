package com.lms.operation.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record DisburseResponse(
        Integer loanId,
        String loanNumber,
        String status,
        BigDecimal principalAmount,
        String currency,
        Integer duration,
        String repaymentFrequency,
        LocalDateTime disbursedAt,
        Integer generatedScheduleCount,
        LocalDate firstDueDate,
        LocalDate lastDueDate
) {
}
