package com.lms.operation.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "employees", indexes = {
        @Index(name = "idx_employees_user",   columnList = "user_id"),
        @Index(name = "idx_employees_branch", columnList = "branch_id")
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id")
    private Integer employeeId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "branch_id")
    private Integer branchId;

    @Column(name = "employee_code", unique = true, length = 50)
    private String employeeCode;

    @Column(name = "position", length = 100)
    private String position;

    @Column(name = "status", length = 20)
    @Builder.Default
    private String status = "Active";

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
