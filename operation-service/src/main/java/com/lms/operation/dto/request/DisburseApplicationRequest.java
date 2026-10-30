package com.lms.operation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Body of POST /api/v1/applications/{id}/disburse. Optional: without it the caller's own employee record is used.")
public class DisburseApplicationRequest {

    // loan officer (employee) who will collect the loan; defaults to the caller's own employee record
    @Schema(description = "Employee id (GET /api/v1/employees) of the loan officer who will collect this loan; "
            + "the loan then shows in GET /api/v1/loan-schedules with this employeeId", example = "1")
    private Integer employeeId;
}
