package com.lms.operation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoanRequestDecisionRequest {

    @NotBlank(message = "Decision is required")
    private String decision;

    @Size(max = 1000, message = "Comment must not exceed 1000 characters")
    private String comment;
}
