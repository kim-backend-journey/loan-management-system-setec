package com.lms.operation.service;

import com.lms.operation.dto.request.ApplicationStatusRequest;
import com.lms.operation.dto.request.CreateApplicationRequest;
import com.lms.operation.dto.request.UpdateApplicationRequest;
import com.lms.operation.dto.response.ApplicationResponse;
import com.lms.operation.dto.response.ApplicationStatusHistoryResponse;

import java.util.List;

public interface LoanApplicationService {

    ApplicationResponse createApplication(CreateApplicationRequest request);

    List<ApplicationResponse> getAllApplications();

    ApplicationResponse getApplicationById(Integer id);

    ApplicationResponse submitApplication(Integer id);

    ApplicationResponse updateApplication(Integer id, UpdateApplicationRequest request);

    ApplicationResponse updateApplicationStatus(Integer id, ApplicationStatusRequest request);

    List<ApplicationStatusHistoryResponse> getApplicationHistory(Integer id);
}