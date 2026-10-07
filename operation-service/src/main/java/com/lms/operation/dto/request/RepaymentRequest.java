package com.lms.operation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RepaymentRequest {

    @NotNull(message = "Loan id is required")
    private Integer loanId;

    @NotNull(message = "Schedule id is required")
    private Integer scheduleId;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    @NotNull(message = "Total amount is required")
    @Positive(message = "Total amount must be positive")
    private BigDecimal totalAmount;

    private String currency = "KHR";

    private Integer bankAccountId;

    private String bankReference;

    @NotNull(message = "Paid at timestamp is required")
    private LocalDateTime paidAt;

    private Integer receivedByEmployeeId;

    private String idempotencyKey;

    private String notes;
}
