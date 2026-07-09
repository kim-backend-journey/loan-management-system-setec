package com.lms.operation.service;

import com.lms.operation.dto.request.CreateCollateralRequest;
import com.lms.operation.dto.response.CollateralResponse;

import java.util.List;

public interface CollateralService {

    CollateralResponse createCollateral(Integer applicationId, CreateCollateralRequest request);

    List<CollateralResponse> getCollaterals(Integer applicationId);
}