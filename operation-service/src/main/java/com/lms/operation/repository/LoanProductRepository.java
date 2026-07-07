package com.lms.operation.repository;

import com.lms.operation.domain.LoanProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanProductRepository extends JpaRepository<LoanProduct, Integer> {

    List<LoanProduct> findByStatus(String status);
}