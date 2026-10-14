package com.lms.operation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class DisburseRequest {

    @NotNull(message = "Disbursement date is required")
    private LocalDate disbursementDate;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    private String bankReference;

    private Integer accountId;

    private String notes;
}
