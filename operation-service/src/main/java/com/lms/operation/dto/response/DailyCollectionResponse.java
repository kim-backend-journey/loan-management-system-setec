package com.lms.operation.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DailyCollectionResponse(
        EmployeeSummaryResponse employee,
        LocalDate date,
        String currency,
        ChannelCollectionResponse cash,
        ChannelCollectionResponse bankTransfer,
        BigDecimal grandTotal
) {
}
