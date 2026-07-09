package com.lms.operation.service;

import com.lms.operation.domain.Collateral;
import com.lms.operation.domain.LoanApplication;
import com.lms.operation.dto.request.CreateCollateralRequest;
import com.lms.operation.dto.response.CollateralResponse;
import com.lms.operation.exception.AppException;
import com.lms.operation.exception.ErrorCode;
import com.lms.operation.repository.CollateralRepository;
import com.lms.operation.repository.LoanApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CollateralServiceImpl implements CollateralService {

    private final CollateralRepository collateralRepository;
    private final LoanApplicationRepository loanApplicationRepository;

    @Override
    @Transactional
    public CollateralResponse createCollateral(Integer applicationId, CreateCollateralRequest request) {
        LoanApplication application = loanApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new AppException(ErrorCode.APPLICATION_NOT_FOUND));

        Collateral collateral = new Collateral();
        collateral.setApplication(application);
        collateral.setCollateralType(request.getCollateralType());
        collateral.setDescription(request.getDescription());
        collateral.setEstimatedValue(request.getEstimatedValue());
        collateral.setOwnerName(request.getOwnerName());
        collateral.setOwnerNationalId(request.getOwnerNationalId());
        collateral.setVerificationStatus("Pending");

        Collateral saved = collateralRepository.save(collateral);
        return CollateralResponse.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CollateralResponse> getCollaterals(Integer applicationId) {
        return collateralRepository.findByApplication_ApplicationId(applicationId).stream()
                .map(CollateralResponse::from)
                .toList();
    }
}