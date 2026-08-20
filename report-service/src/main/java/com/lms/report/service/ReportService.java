package com.lms.report.service;

import com.lms.report.dto.response.DashboardResponse;
import com.lms.report.dto.response.LoanReportResponse;
import com.lms.report.dto.response.PaymentReportResponse;

import java.util.List;

public interface ReportService {
    DashboardResponse getDashboard();
    List<LoanReportResponse> getLoanReports();
    List<PaymentReportResponse> getPaymentReports();
}