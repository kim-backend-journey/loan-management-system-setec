package com.lms.operation.service;

import com.lms.operation.dto.request.CreateLoanRequestRequest;
import com.lms.operation.dto.request.LoanRequestDecisionRequest;
import com.lms.operation.dto.response.LoanRequestDecisionResponse;
import com.lms.operation.dto.response.LoanRequestResponse;

public interface LoanRequestService {

    LoanRequestResponse createLoanRequest(CreateLoanRequestRequest request);

    LoanRequestDecisionResponse decideRequest(Integer loanRequestId, LoanRequestDecisionRequest request);
}
