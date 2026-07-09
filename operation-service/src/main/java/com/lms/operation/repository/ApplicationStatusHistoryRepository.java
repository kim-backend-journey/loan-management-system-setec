package com.lms.operation.repository;

import com.lms.operation.domain.ApplicationStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationStatusHistoryRepository extends JpaRepository<ApplicationStatusHistory, Integer> {

    List<ApplicationStatusHistory> findByApplication_ApplicationId(Integer applicationId);
}