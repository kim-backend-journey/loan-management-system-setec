package com.lms.operation.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record EmploymentResponse(
        Integer employmentId,
        Integer customerId,
        String employerName,
        String occupation,
        BigDecimal monthlyIncome,
        LocalDate startDate,
        LocalDate endDate,
        Boolean isCurrent,
        LocalDateTime createdAt
) {}