package com.lms.operation.dto.response;

import java.util.List;

/** Same page shape as {@link PaginatedLoanScheduleResponse}: 1-based page, items, totals. */
public record PaginatedDueInstallmentResponse(
        List<DueInstallmentResponse> items,
        int page,
        int size,
        long totalItems,
        int totalPages
) {
}
