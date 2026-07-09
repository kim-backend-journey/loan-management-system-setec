package com.lms.operation.repository;

import com.lms.operation.domain.LoanPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanPaymentRepository extends JpaRepository<LoanPayment, Integer> {
    List<LoanPayment> findByLoan_LoanId(Integer loanId);
}