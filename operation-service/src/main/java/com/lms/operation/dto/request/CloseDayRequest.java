package com.lms.operation.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CloseDayRequest {

    @NotNull(message = "Business date is required")
    private LocalDate businessDate;

    @NotNull(message = "Counted cash is required")
    @PositiveOrZero(message = "Counted cash cannot be negative")
    private BigDecimal cashCounted;

    // required when the counted cash does not match the ledger
    private String notes;
}
