package com.lms.operation.repository;

import com.lms.operation.domain.DailyClosing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface DailyClosingRepository extends JpaRepository<DailyClosing, Integer> {

    Optional<DailyClosing> findByEmployeeIdAndBusinessDate(Integer employeeId, LocalDate businessDate);

    boolean existsByEmployeeIdAndBusinessDate(Integer employeeId, LocalDate businessDate);
}
