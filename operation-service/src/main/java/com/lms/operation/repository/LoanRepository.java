package com.lms.operation.repository;

import com.lms.operation.domain.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface LoanRepository extends JpaRepository<Loan, Integer>,
        JpaSpecificationExecutor<Loan> {
    Optional<Loan> findByApplication_ApplicationId(Integer applicationId);
    Optional<Loan> findByLoanNumber(String loanNumber);
    boolean existsByLoanRequestId(Integer loanRequestId);
}