package com.lms.report.repository;

import com.lms.report.dto.projection.LoanReportProjection;
import com.lms.report.dto.projection.OfficerReportProjection;
import com.lms.report.dto.projection.PaymentReportProjection;
import com.lms.report.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
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

    // ---------- Officer (CO) report ----------

    @Query(value = """
            SELECT e.employee_id FROM employees e
            JOIN users u ON u.user_id = e.user_id
            WHERE u.username = :username
            LIMIT 1
            """, nativeQuery = true)
    Integer findEmployeeIdByUsername(@Param("username") String username);

    // Collections are credited to the loan's officer (not whoever keyed the payment in).
    // Status values are mixed case across the two disbursement paths, so they are compared upper-cased.
    @Query(value = """
            SELECT e.employee_id                                   AS employeeId,
                   e.employee_code                                 AS employeeCode,
                   NULLIF(TRIM(COALESCE(u.first_name, '') || ' ' || COALESCE(u.last_name, '')), '') AS officerName,
                   e.branch_id                                     AS branchId,
                   COALESCE(d.loans_disbursed, 0)                  AS loansDisbursed,
                   COALESCE(d.amount_disbursed, 0)                 AS amountDisbursed,
                   COALESCE(c.payments, 0)                         AS paymentsCount,
                   COALESCE(c.total, 0)                            AS totalCollected,
                   COALESCE(c.principal, 0)                        AS principalCollected,
                   COALESCE(c.interest, 0)                         AS interestCollected,
                   COALESCE(c.penalty, 0)                          AS penaltyCollected,
                   COALESCE(p.active_loans, 0)                     AS activeLoans,
                   COALESCE(p.outstanding, 0)                      AS outstandingBalance,
                   COALESCE(o.overdue_count, 0)                    AS overdueInstallments,
                   COALESCE(o.overdue_amount, 0)                   AS overdueAmount
            FROM employees e
            LEFT JOIN users u ON u.user_id = e.user_id
            LEFT JOIN (
                SELECT employee_id, COUNT(*) AS loans_disbursed, SUM(approved_amount) AS amount_disbursed
                FROM loans
                WHERE start_date BETWEEN :fromDate AND :toDate
                GROUP BY employee_id
            ) d ON d.employee_id = e.employee_id
            LEFT JOIN (
                SELECT l.employee_id, COUNT(*) AS payments, SUM(cl.total_amount) AS total,
                       SUM(cl.principal_amount) AS principal, SUM(cl.interest_amount) AS interest,
                       SUM(cl.penalty_amount) AS penalty
                FROM collection_ledger cl
                JOIN loan_payments lp ON lp.payment_id = cl.payment_id
                JOIN loans l ON l.loan_id = lp.loan_id
                WHERE cl.business_date BETWEEN :fromDate AND :toDate
                  AND COALESCE(cl.is_voided, FALSE) = FALSE
                GROUP BY l.employee_id
            ) c ON c.employee_id = e.employee_id
            LEFT JOIN (
                SELECT employee_id, COUNT(*) AS active_loans, SUM(outstanding_balance) AS outstanding
                FROM loans
                WHERE UPPER(COALESCE(loan_status, 'ACTIVE')) NOT IN ('COMPLETED', 'CLOSED', 'CANCELLED')
                GROUP BY employee_id
            ) p ON p.employee_id = e.employee_id
            LEFT JOIN (
                SELECT l.employee_id, COUNT(*) AS overdue_count,
                       SUM(GREATEST(lp.amount_due + COALESCE(lp.penalty_amount, 0) + COALESCE(lp.late_fee, 0)
                                    - COALESCE(lp.amount_paid, 0), 0)) AS overdue_amount
                FROM loan_payments lp
                JOIN loans l ON l.loan_id = lp.loan_id
                WHERE lp.due_date < :today
                  AND UPPER(COALESCE(lp.payment_status, 'PENDING')) NOT IN ('PAID', 'CANCELLED', 'WAIVED')
                  AND UPPER(COALESCE(l.loan_status, 'ACTIVE')) NOT IN ('COMPLETED', 'CLOSED', 'CANCELLED')
                GROUP BY l.employee_id
            ) o ON o.employee_id = e.employee_id
            WHERE (CAST(:employeeId AS INTEGER) IS NULL OR e.employee_id = CAST(:employeeId AS INTEGER))
            ORDER BY COALESCE(c.total, 0) DESC, e.employee_id
            """, nativeQuery = true)
    List<OfficerReportProjection> findOfficerReport(@Param("fromDate") LocalDate fromDate,
                                                    @Param("toDate") LocalDate toDate,
                                                    @Param("today") LocalDate today,
                                                    @Param("employeeId") Integer employeeId);
}