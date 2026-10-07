package com.lms.operation.dto.response;

import java.util.List;

public record PaginatedLoanScheduleResponse(
        List<LoanScheduleItemResponse> items,
        int page,
        int size,
        long totalItems,
        int totalPages
) {
}
