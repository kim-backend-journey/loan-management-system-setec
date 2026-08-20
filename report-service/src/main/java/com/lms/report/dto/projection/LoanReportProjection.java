package com.lms.report.dto.projection;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface LoanReportProjection {
    Long getLoanId();
    String getLoanNumber();
    String getCustomerName();
    BigDecimal getApprovedAmount();
    BigDecimal getOutstandingBalance();
    String getLoanStatus();
    LocalDate getStartDate();
    LocalDate getEndDate();
}