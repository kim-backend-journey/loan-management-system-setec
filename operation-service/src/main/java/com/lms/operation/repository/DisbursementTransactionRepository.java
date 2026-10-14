package com.lms.operation.repository;

import com.lms.operation.domain.DisbursementTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DisbursementTransactionRepository
        extends JpaRepository<DisbursementTransaction, Integer> {

    boolean existsByBankReference(String bankReference);

    boolean existsByLoanRequestId(Integer loanRequestId);

    Optional<DisbursementTransaction> findByLoanRequestId(Integer loanRequestId);
}
