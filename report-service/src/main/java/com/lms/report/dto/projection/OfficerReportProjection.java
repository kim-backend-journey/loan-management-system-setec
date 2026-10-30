package com.lms.report.dto.projection;

import java.math.BigDecimal;

public interface OfficerReportProjection {
    Integer getEmployeeId();
    String getEmployeeCode();
    String getOfficerName();
    Integer getBranchId();
    Long getLoansDisbursed();
    BigDecimal getAmountDisbursed();
    Long getPaymentsCount();
    BigDecimal getTotalCollected();
    BigDecimal getPrincipalCollected();
    BigDecimal getInterestCollected();
    BigDecimal getPenaltyCollected();
    Long getActiveLoans();
    BigDecimal getOutstandingBalance();
    Long getOverdueInstallments();
    BigDecimal getOverdueAmount();
}
