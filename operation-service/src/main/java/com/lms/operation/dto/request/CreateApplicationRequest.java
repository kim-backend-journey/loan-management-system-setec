package com.lms.operation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateApplicationRequest {

    @NotNull(message = "Customer id is required")
    private Integer customerId;

    @NotNull(message = "Loan product id is required")
    private Integer loanProductId;

    @NotNull(message = "Requested amount is required")
    @Positive(message = "Requested amount must be positive")
    private BigDecimal requestedAmount;

    @NotNull(message = "Requested term (months) is required")
    @Positive(message = "Requested term must be positive")
    private Integer requestedTermMonths;

    @NotBlank(message = "Loan purpose is required")
    private String loanPurpose;
}