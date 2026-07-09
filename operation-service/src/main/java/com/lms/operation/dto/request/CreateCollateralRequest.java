package com.lms.operation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateCollateralRequest {

    @NotBlank(message = "collateralType is required")
    private String collateralType;

    private String description;

    @NotNull(message = "estimatedValue is required")
    private BigDecimal estimatedValue;

    private String ownerName;

    private String ownerNationalId;
}