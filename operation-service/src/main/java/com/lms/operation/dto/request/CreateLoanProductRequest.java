package com.lms.operation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateLoanProductRequest {

    @NotBlank(message = "Product name is required")
    private String productName;

    @NotNull(message = "Max loan amount is required")
    @Positive(message = "Max loan amount must be positive")
    private BigDecimal maxLoanAmount;

    @NotNull(message = "Minimum income is required")
    @Positive(message = "Minimum income must be positive")
    private BigDecimal minimumIncome;

    @NotNull(message = "Interest rate is required")
    @Positive(message = "Interest rate must be positive")
    private BigDecimal interestRate;

    @NotNull(message = "Minimum term (months) is required")
    @Positive(message = "Minimum term must be positive")
    private Integer minimumTermMonths;

    @NotNull(message = "Maximum term (months) is required")
    @Positive(message = "Maximum term must be positive")
    private Integer maximumTermMonths;

    @NotNull(message = "Processing fee is required")
    private BigDecimal processingFee;

    private String description;
}