package com.lms.operation.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record SchedulePaymentItemResponse(
        Integer scheduleId,
        Integer paymentNo,
        LocalDate dueDate,
        BigDecimal amountDue,
        BigDecimal amountPaid,
        BigDecimal remainingBalance,
        String paymentStatus,
        LocalDateTime paymentDate,
        BigDecimal lateFee,
        BigDecimal penaltyAmount
) {
}
