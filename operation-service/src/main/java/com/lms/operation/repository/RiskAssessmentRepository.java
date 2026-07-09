package com.lms.operation.repository;

import com.lms.operation.domain.RiskAssessment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RiskAssessmentRepository extends JpaRepository<RiskAssessment, Integer> {
    Optional<RiskAssessment> findByApplication_ApplicationId(Integer applicationId);
}