package com.lms.report.service;

import com.lms.report.dto.response.DashboardResponse;
import com.lms.report.dto.response.LoanReportResponse;
import com.lms.report.dto.response.PaymentReportResponse;
import com.lms.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;

    @Override
    public DashboardResponse getDashboard() {
        return new DashboardResponse(
                reportRepository.countCustomers(),
                reportRepository.countApplications(),
                reportRepository.countLoans(),
                reportRepository.countActiveLoans(),
                reportRepository.countCompletedLoans(),
                reportRepository.countDefaultedLoans(),
                reportRepository.sumTotalDisbursed(),
                reportRepository.sumTotalCollected(),
                reportRepository.countOverduePayments()
        );
    }

    @Override
    public List<LoanReportResponse> getLoanReports() {
        return reportRepository.findAllLoanReports().stream()
                .map(p -> new LoanReportResponse(
                        p.getLoanId(),
                        p.getLoanNumber(),
                        p.getCustomerName(),
                        p.getApprovedAmount(),
                        p.getOutstandingBalance(),
                        p.getLoanStatus(),
                        p.getStartDate(),
                        p.getEndDate()))
                .toList();
    }

    @Override
    public List<PaymentReportResponse> getPaymentReports() {
        return reportRepository.findAllPaymentReports().stream()
                .map(p -> new PaymentReportResponse(
                        p.getPaymentId(),
                        p.getLoanNumber(),
                        p.getDueDate(),
                        p.getAmountDue(),
                        p.getAmountPaid(),
                        p.getPaymentStatus(),
                        p.getPaymentDate()))
                .toList();
    }
}