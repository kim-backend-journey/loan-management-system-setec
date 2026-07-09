package com.lms.operation.repository;

import com.lms.operation.domain.LoanApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanApplicationRepository extends JpaRepository<LoanApplication, Integer> {

    List<LoanApplication> findByCustomer_CustomerId(Integer customerId);

    List<LoanApplication> findByApplicationStatus(String applicationStatus);
}