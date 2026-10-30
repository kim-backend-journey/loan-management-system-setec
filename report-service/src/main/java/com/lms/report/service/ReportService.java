package com.lms.report.service;

import com.lms.report.dto.response.DashboardResponse;
import com.lms.report.dto.response.LoanReportResponse;
import com.lms.report.dto.response.OfficerReportResponse;
import com.lms.report.dto.response.PaymentReportResponse;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {
    DashboardResponse getDashboard();
    List<LoanReportResponse> getLoanReports();
    List<PaymentReportResponse> getPaymentReports();

    /** Officer performance for the WEEK, MONTH or YEAR containing {@code date}. Officers only see themselves. */
    OfficerReportResponse getOfficerReport(String period, LocalDate date, Integer employeeId);
}