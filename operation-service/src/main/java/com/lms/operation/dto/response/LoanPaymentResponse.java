package com.lms.operation.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record LoanPaymentResponse(
        Integer paymentId,
        Integer loanId,
        Integer paymentNo,
        LocalDate dueDate,
        BigDecimal amountDue,
        BigDecimal amountPaid,
        BigDecimal remainingBalance,
        LocalDateTime paymentDate,
        String paymentStatus,
        String paidBy,
        BigDecimal lateFee,
        BigDecimal penaltyAmount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}