package com.lms.operation.service;

import com.lms.operation.dto.request.CreateGuarantorRequest;
import com.lms.operation.dto.request.VerifyGuarantorRequest;
import com.lms.operation.dto.response.GuarantorResponse;

import java.util.List;

public interface GuarantorService {

    GuarantorResponse createGuarantor(Integer applicationId, CreateGuarantorRequest request);

    List<GuarantorResponse> getGuarantors(Integer applicationId);

    GuarantorResponse verifyGuarantor(Integer applicationId, Integer guarantorId, VerifyGuarantorRequest request);
}