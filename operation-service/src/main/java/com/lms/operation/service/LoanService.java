package com.lms.operation.service;

import com.lms.operation.dto.response.LoanResponse;

public interface LoanService {
    LoanResponse disburse(Integer applicationId);
    LoanResponse getLoan(Integer loanId);
}