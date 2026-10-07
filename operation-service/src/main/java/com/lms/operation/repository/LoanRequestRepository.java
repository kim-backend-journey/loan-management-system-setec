package com.lms.operation.repository;

import com.lms.operation.domain.LoanRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRequestRepository extends JpaRepository<LoanRequest, Integer> {

    List<LoanRequest> findByCustomer_CustomerId(Integer customerId);

    List<LoanRequest> findByStatus(String status);

    boolean existsByLoanRequestNumber(String loanRequestNumber);
}
