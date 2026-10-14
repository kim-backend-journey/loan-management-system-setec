package com.lms.operation.dto.response;

import java.math.BigDecimal;

public record AllocationResponse(
        BigDecimal penalty,
        BigDecimal serviceFee,
        BigDecimal interest,
        BigDecimal principal
) {
}
