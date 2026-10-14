package com.lms.operation.service;

import com.lms.operation.dto.request.DisburseRequest;
import com.lms.operation.dto.response.DisburseResponse;

public interface DisburseService {

    DisburseResponse disburse(Integer loanRequestId, DisburseRequest request);
}
