package com.lms.operation.service;

import com.lms.operation.dto.response.LoanResponse;

public interface LoanService {
    LoanResponse disburse(Integer applicationId, Integer employeeId);
    LoanResponse getLoan(Integer loanId);
}