package com.lms.operation.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateLoanProductRequest {

    private String productName;
    private BigDecimal maxLoanAmount;
    private BigDecimal minimumIncome;
    private BigDecimal interestRate;
    private Integer minimumTermMonths;
    private Integer maximumTermMonths;
    private BigDecimal processingFee;
    private String description;
}