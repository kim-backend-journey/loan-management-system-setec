package com.lms.operation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LoanProductStatusRequest {

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "Active|Inactive", message = "Status must be either 'Active' or 'Inactive'")
    private String status;
}