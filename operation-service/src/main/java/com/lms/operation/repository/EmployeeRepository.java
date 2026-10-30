package com.lms.operation.repository;

import com.lms.operation.domain.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    Optional<Employee> findByUserId(Integer userId);

    Optional<Employee> findByEmployeeCode(String employeeCode);

    /**
     * Gives every OFFICER user without one an employee record, so officers created in auth-service
     * can be assigned loans and collect straight away. Same statement as the backfill in db/upgrade.sql.
     */
    @Modifying
    @Query(value = """
            INSERT INTO employees (user_id, employee_code, position, status, created_at, updated_at)
            SELECT u.user_id, 'EMP-U' || LPAD(u.user_id::text, 4, '0'), 'Loan Officer', 'Active', NOW(), NOW()
            FROM users u
            JOIN roles r ON r.role_id = u.role_id
            WHERE r.role_name = 'OFFICER'
              AND NOT EXISTS (SELECT 1 FROM employees e WHERE e.user_id = u.user_id)
            ON CONFLICT (employee_code) DO NOTHING
            """, nativeQuery = true)
    int provisionOfficers();
}
