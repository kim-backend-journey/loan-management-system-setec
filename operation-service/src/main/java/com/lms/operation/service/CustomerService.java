package com.lms.operation.service;

import com.lms.operation.dto.request.CreateCustomerRequest;
import com.lms.operation.dto.request.EmploymentRequest;
import com.lms.operation.dto.request.UpdateCustomerRequest;
import com.lms.operation.dto.response.CustomerResponse;
import com.lms.operation.dto.response.EmploymentResponse;

import java.util.List;

public interface CustomerService {

    CustomerResponse createCustomer(CreateCustomerRequest request);

    List<CustomerResponse> getAllCustomers();

    CustomerResponse getCustomerById(Integer customerId);

    CustomerResponse updateCustomer(Integer customerId,
                                    UpdateCustomerRequest request);

    List<EmploymentResponse> getEmploymentHistory(Integer customerId);

    EmploymentResponse addEmployment(Integer customerId,
                                     EmploymentRequest request);

    EmploymentResponse updateEmployment(Integer customerId,
                                        Integer employmentId,
                                        EmploymentRequest request);
}