package com.lms.operation.repository;

import com.lms.operation.domain.LoanApproval;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoanApprovalRepository extends JpaRepository<LoanApproval, Integer> {
    Optional<LoanApproval> findByApplication_ApplicationId(Integer applicationId);
}