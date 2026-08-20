package com.lms.report.dto.projection;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface PaymentReportProjection {
    Long getPaymentId();
    String getLoanNumber();
    LocalDate getDueDate();
    BigDecimal getAmountDue();
    BigDecimal getAmountPaid();
    String getPaymentStatus();
    LocalDate getPaymentDate();
}