package com.lms.operation.service;

import com.lms.operation.dto.request.RecordPaymentRequest;
import com.lms.operation.dto.response.LoanPaymentResponse;

import java.util.List;

public interface LoanPaymentService {
    List<LoanPaymentResponse> getPayments(Integer loanId);
    LoanPaymentResponse getPayment(Integer loanId, Integer paymentId);
    LoanPaymentResponse recordPayment(Integer loanId, Integer paymentId, RecordPaymentRequest request);
}