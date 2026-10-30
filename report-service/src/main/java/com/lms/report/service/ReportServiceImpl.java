package com.lms.report.service;

import com.lms.report.dto.response.DashboardResponse;
import com.lms.report.dto.response.LoanReportResponse;
import com.lms.report.dto.response.OfficerReportResponse;
import com.lms.report.dto.response.PaymentReportResponse;
import com.lms.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
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
    public OfficerReportResponse getOfficerReport(String period, LocalDate date, Integer employeeId) {
        LocalDate anchor = date == null ? LocalDate.now() : date;
        String unit = period == null ? "MONTH" : period.trim().toUpperCase();
        LocalDate from;
        LocalDate to;
        switch (unit) {
            case "WEEK" -> {
                from = anchor.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                to = from.plusDays(6);
            }
            case "MONTH" -> {
                from = anchor.withDayOfMonth(1);
                to = anchor.with(TemporalAdjusters.lastDayOfMonth());
            }
            case "YEAR" -> {
                from = anchor.withDayOfYear(1);
                to = anchor.with(TemporalAdjusters.lastDayOfYear());
            }
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "period must be WEEK, MONTH or YEAR");
        }

        // Officers only ever see their own row; a user with no employee record sees nothing.
        Integer scope = employeeId;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean admin = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        if (!admin) {
            scope = auth == null ? null : reportRepository.findEmployeeIdByUsername(auth.getName());
            if (scope == null) {
                return new OfficerReportResponse(unit, from, to, List.of());
            }
        }

        List<OfficerReportResponse.Row> rows = reportRepository
                .findOfficerReport(from, to, LocalDate.now(), scope).stream()
                .map(p -> new OfficerReportResponse.Row(
                        p.getEmployeeId(),
                        p.getEmployeeCode(),
                        p.getOfficerName(),
                        p.getBranchId(),
                        nz(p.getLoansDisbursed()),
                        p.getAmountDisbursed(),
                        nz(p.getPaymentsCount()),
                        p.getTotalCollected(),
                        p.getPrincipalCollected(),
                        p.getInterestCollected(),
                        p.getPenaltyCollected(),
                        nz(p.getActiveLoans()),
                        p.getOutstandingBalance(),
                        nz(p.getOverdueInstallments()),
                        p.getOverdueAmount()))
                .toList();
        return new OfficerReportResponse(unit, from, to, rows);
    }

    private static long nz(Long value) {
        return value == null ? 0 : value;
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