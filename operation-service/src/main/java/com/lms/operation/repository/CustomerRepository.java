package com.lms.operation.repository;

import com.lms.operation.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    Optional<Customer> findByEmail(String email);
    Optional<Customer> findByNationalId(String nationalId);
    Optional<Customer> findByPhoneNumber(String phoneNumber);
    boolean existsByEmail(String email);
    boolean existsByNationalId(String nationalId);
    boolean existsByPhoneNumber(String phoneNumber);
}