package com.lms.report.repository;

import com.lms.report.dto.projection.LoanReportProjection;
import com.lms.report.dto.projection.PaymentReportProjection;
import com.lms.report.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Customer, Long> {

    // ---------- Dashboard COUNT / SUM queries ----------

    @Query(value = "SELECT COUNT(*) FROM customers", nativeQuery = true)
    long countCustomers();

    @Query(value = "SELECT COUNT(*) FROM loan_applications", nativeQuery = true)
    long countApplications();

    @Query(value = "SELECT COUNT(*) FROM loans", nativeQuery = true)
    long countLoans();

    @Query(value = "SELECT COUNT(*) FROM loans WHERE loan_status = 'Active'", nativeQuery = true)
    long countActiveLoans();

    @Query(value = "SELECT COUNT(*) FROM loans WHERE loan_status = 'Completed'", nativeQuery = true)
    long countCompletedLoans();

    @Query(value = "SELECT COUNT(*) FROM loans WHERE loan_status = 'Defaulted'", nativeQuery = true)
    long countDefaultedLoans();

    @Query(value = "SELECT COALESCE(SUM(approved_amount), 0) FROM loans", nativeQuery = true)
    BigDecimal sumTotalDisbursed();

    @Query(value = "SELECT COALESCE(SUM(amount_paid), 0) FROM loan_payments WHERE payment_status = 'Paid'",
            nativeQuery = true)
    BigDecimal sumTotalCollected();

    @Query(value = "SELECT COUNT(*) FROM loan_payments WHERE payment_status = 'Overdue'", nativeQuery = true)
    long countOverduePayments();

    // ---------- Loan report ----------

    @Query(value = """
            SELECT l.loan_id                               AS loanId,
                   l.loan_number                           AS loanNumber,
                   c.first_name || ' ' || c.last_name      AS customerName,
                   l.approved_amount                       AS approvedAmount,
                   l.outstanding_balance                   AS outstandingBalance,
                   l.loan_status                           AS loanStatus,
                   l.start_date                            AS startDate,
                   l.end_date                              AS endDate
            FROM loans l
            JOIN loan_applications la ON la.application_id = l.application_id
            JOIN customers c ON c.customer_id = la.customer_id
            ORDER BY l.loan_id DESC
            """, nativeQuery = true)
    List<LoanReportProjection> findAllLoanReports();

    // ---------- Payment report ----------

    @Query(value = """
            SELECT p.payment_id      AS paymentId,
                   l.loan_number     AS loanNumber,
                   p.due_date        AS dueDate,
                   p.amount_due      AS amountDue,
                   p.amount_paid     AS amountPaid,
                   p.payment_status  AS paymentStatus,
                   CAST(p.payment_date AS date) AS paymentDate
            FROM loan_payments p
            JOIN loans l ON l.loan_id = p.loan_id
            ORDER BY p.due_date DESC
            """, nativeQuery = true)
    List<PaymentReportProjection> findAllPaymentReports();
}