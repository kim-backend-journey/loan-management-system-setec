package com.lms.operation.repository;

import com.lms.operation.domain.CollectionLedger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CollectionLedgerRepository extends JpaRepository<CollectionLedger, Integer> {

    Optional<CollectionLedger> findByTransactionId(Integer transactionId);

    List<CollectionLedger> findByEmployeeIdAndBusinessDateAndIsVoided(
            Integer employeeId, LocalDate businessDate, Boolean isVoided);
}
