package com.lms.operation.service;

import com.lms.operation.dto.request.CreateLoanProductRequest;
import com.lms.operation.dto.request.LoanProductStatusRequest;
import com.lms.operation.dto.request.UpdateLoanProductRequest;
import com.lms.operation.dto.response.LoanProductResponse;

import java.util.List;

public interface LoanProductService {

    LoanProductResponse createLoanProduct(CreateLoanProductRequest request);

    List<LoanProductResponse> getAllLoanProducts();

    LoanProductResponse getLoanProductById(Integer id);

    LoanProductResponse updateLoanProduct(Integer id, UpdateLoanProductRequest request);

    LoanProductResponse updateLoanProductStatus(Integer id, LoanProductStatusRequest request);
}