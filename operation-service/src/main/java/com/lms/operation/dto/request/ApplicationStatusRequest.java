package com.lms.operation.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ApplicationStatusRequest {

    @NotBlank(message = "Status is required")
    private String status;

    private String remark;
}