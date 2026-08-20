package com.lms.report.dto.response;

import java.math.BigDecimal;

public record DashboardResponse(
        Long totalCustomers,
        Long totalApplications,
        Long totalLoans,
        Long activeLoans,
        Long completedLoans,
        Long defaultedLoans,
        BigDecimal totalDisbursed,
        BigDecimal totalCollected,
        Long overduePayments
) {}