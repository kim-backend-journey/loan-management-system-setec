package com.lms.operation.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateApplicationRequest {

    private Integer customerId;
    private Integer loanProductId;
    private BigDecimal requestedAmount;
    private Integer requestedTermMonths;
    private String loanPurpose;
}