package com.lms.operation.repository;

import com.lms.operation.domain.CollectionLedger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CollectionLedgerRepository extends JpaRepository<CollectionLedger, Integer> {

    Optional<CollectionLedger> findByTransactionId(Integer transactionId);

    List<CollectionLedger> findByEmployeeIdAndBusinessDateAndIsVoided(
            Integer employeeId, LocalDate businessDate, Boolean isVoided);

    /** Interest already collected on one installment across all its (non-voided) payments. */
    @Query("""
            select coalesce(sum(c.interestAmount), 0) from CollectionLedger c
            where c.paymentId = :paymentId and (c.isVoided is null or c.isVoided = false)
            """)
    BigDecimal sumInterestCollected(@Param("paymentId") Integer paymentId);

    /** Penalty already collected on one installment across all its (non-voided) payments. */
    @Query("""
            select coalesce(sum(c.penaltyAmount), 0) from CollectionLedger c
            where c.paymentId = :paymentId and (c.isVoided is null or c.isVoided = false)
            """)
    BigDecimal sumPenaltyCollected(@Param("paymentId") Integer paymentId);
}
