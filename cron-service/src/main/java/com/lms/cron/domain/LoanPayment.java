package com.lms.cron.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "loan_payments")
public class LoanPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Integer paymentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id")
    private Loan loan;

    @Column(name = "payment_no")
    private Integer paymentNo;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "amount_due")
    private BigDecimal amountDue;

    @Column(name = "payment_status")
    private String paymentStatus;
}