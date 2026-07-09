package com.lms.operation.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentTransactionResponse(
        Integer transactionId,
        Integer paymentId,
        String paymentMethod,
        BigDecimal amount,
        String referenceNumber,
        String transactionStatus,
        Integer receivedBy,
        LocalDateTime transactionDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}