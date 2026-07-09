package com.lms.operation.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class EmploymentRequest {

    @NotBlank(message = "Employer name is required")
    private String employerName;

    private String occupation;
    private BigDecimal monthlyIncome;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isCurrent;
}