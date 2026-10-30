package com.lms.operation.repository;

import com.lms.operation.domain.LoanPayment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface LoanPaymentRepository extends JpaRepository<LoanPayment, Integer> {
    List<LoanPayment> findByLoan_LoanId(Integer loanId);

    /**
     * Installments still owed on active loans whose due date falls in [from, to], earliest first.
     * Status values are mixed case across the two disbursement paths, so they are compared upper-cased.
     */
    @Query(value = """
            select p from LoanPayment p join fetch p.loan l
            where upper(coalesce(p.paymentStatus, 'PENDING')) not in ('PAID', 'CANCELLED', 'WAIVED')
              and upper(coalesce(l.loanStatus, 'ACTIVE')) not in ('COMPLETED', 'CLOSED', 'CANCELLED')
              and p.dueDate >= :from and p.dueDate <= :to
              and (:employeeId is null or l.employeeId = :employeeId)
            order by p.dueDate asc, l.loanId asc, p.paymentNo asc
            """,
            countQuery = """
            select count(p) from LoanPayment p join p.loan l
            where upper(coalesce(p.paymentStatus, 'PENDING')) not in ('PAID', 'CANCELLED', 'WAIVED')
              and upper(coalesce(l.loanStatus, 'ACTIVE')) not in ('COMPLETED', 'CLOSED', 'CANCELLED')
              and p.dueDate >= :from and p.dueDate <= :to
              and (:employeeId is null or l.employeeId = :employeeId)
            """)
    Page<LoanPayment> findOpenInstallmentsDueBetween(@Param("from") LocalDate from,
                                                     @Param("to") LocalDate to,
                                                     @Param("employeeId") Integer employeeId,
                                                     Pageable pageable);
}