package com.lms.report.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentReportResponse(
        Long paymentId,
        String loanNumber,
        LocalDate dueDate,
        BigDecimal amountDue,
        BigDecimal amountPaid,
        String paymentStatus,
        LocalDate paymentDate
) {}