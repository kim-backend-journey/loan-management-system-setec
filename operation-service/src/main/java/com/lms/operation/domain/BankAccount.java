package com.lms.operation.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bank_accounts")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class BankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bank_account_id")
    private Integer bankAccountId;

    @Column(name = "account_number", unique = true, length = 100)
    private String accountNumber;

    @Column(name = "account_name", length = 255)
    private String accountName;

    @Column(name = "bank_name", length = 255)
    private String bankName;

    @Column(name = "currency", length = 10)
    private String currency;

    @Column(name = "status", length = 20)
    private String status;
}
