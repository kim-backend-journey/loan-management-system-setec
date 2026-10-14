package com.lms.operation.service;

import com.lms.operation.dto.request.RepaymentRequest;
import com.lms.operation.dto.response.RepaymentResponse;

public interface RepaymentService {

    RepaymentResult createRepayment(RepaymentRequest request);

    /**
     * Wraps the response with whether it was an idempotent replay of an
     * already-processed request (replay -> HTTP 200, new -> HTTP 201).
     */
    record RepaymentResult(RepaymentResponse response, boolean replay) {
    }
}
