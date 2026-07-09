package com.lms.operation.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RecordPaymentRequest {
    private String paymentMethod;
    private BigDecimal amount;
    private String referenceNumber;
}