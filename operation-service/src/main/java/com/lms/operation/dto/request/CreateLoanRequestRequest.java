package com.lms.operation.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateLoanRequestRequest {

    @NotNull(message = "Customer id is required")
    private Integer customerId;

    @NotNull(message = "Loan product id is required")
    private Integer loanProductId;

    private Integer employeeId;

    private Integer branchId;

    @NotNull(message = "Principal amount is required")
    @Positive(message = "Principal amount must be positive")
    private BigDecimal principalAmount;

    private String currency = "KHR";

    @NotNull(message = "Duration is required")
    @Positive(message = "Duration must be positive")
    private Integer duration;

    private String durationUnit = "MONTH";

    private String repaymentFrequency = "MONTHLY";

    @NotNull(message = "Monthly payment amount is required")
    @Positive(message = "Monthly payment amount must be positive")
    private BigDecimal monthlyPaymentAmount;

    private BigDecimal serviceFeeAmount;

    private String purpose;

    private LocalDate requestedDisbursementDate;

    private String notes;
}
