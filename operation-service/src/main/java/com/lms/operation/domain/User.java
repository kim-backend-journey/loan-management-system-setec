package com.lms.operation.domain;

import jakarta.persistence.*;
import lombok.*;

/**
 * Read-only view of the shared {@code users} table (owned by auth-service),
 * used here only to resolve an employee's display name.
 */
@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class User {

    @Id
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "username", insertable = false, updatable = false)
    private String username;

    @Column(name = "first_name", insertable = false, updatable = false)
    private String firstName;

    @Column(name = "last_name", insertable = false, updatable = false)
    private String lastName;
}
