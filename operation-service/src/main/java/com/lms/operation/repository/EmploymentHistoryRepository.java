package com.lms.operation.repository;

import com.lms.operation.domain.EmploymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmploymentHistoryRepository
        extends JpaRepository<EmploymentHistory, Integer> {

    List<EmploymentHistory> findByCustomer_CustomerId(Integer customerId);
}