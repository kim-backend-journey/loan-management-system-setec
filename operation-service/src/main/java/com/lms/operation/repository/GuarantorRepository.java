package com.lms.operation.repository;

import com.lms.operation.domain.Guarantor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GuarantorRepository extends JpaRepository<Guarantor, Integer> {

    List<Guarantor> findByApplication_ApplicationId(Integer applicationId);
}