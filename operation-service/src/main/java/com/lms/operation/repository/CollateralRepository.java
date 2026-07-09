package com.lms.operation.repository;

import com.lms.operation.domain.Collateral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollateralRepository extends JpaRepository<Collateral, Integer> {

    List<Collateral> findByApplication_ApplicationId(Integer applicationId);
}