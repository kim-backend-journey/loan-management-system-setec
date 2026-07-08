package com.lms.operation.dto.response;

import java.time.LocalDateTime;

public record ApplicationStatusHistoryResponse(
        Integer historyId,
        Integer applicationId,
        Integer changedBy,
        String oldStatus,
        String newStatus,
        String remark,
        LocalDateTime createdAt
) {
}