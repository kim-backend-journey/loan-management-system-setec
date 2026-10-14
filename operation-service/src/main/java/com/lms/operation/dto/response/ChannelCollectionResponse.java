package com.lms.operation.dto.response;

import java.math.BigDecimal;

public record ChannelCollectionResponse(
        Integer transactionCount,
        BigDecimal principal,
        BigDecimal interest,
        BigDecimal serviceFee,
        BigDecimal penalty,
        BigDecimal total,
        BigDecimal pendingVerificationAmount
) {
}
