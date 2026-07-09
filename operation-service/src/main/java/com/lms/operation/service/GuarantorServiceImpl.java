package com.lms.operation.service;

import com.lms.operation.domain.Guarantor;
import com.lms.operation.domain.LoanApplication;
import com.lms.operation.dto.request.CreateGuarantorRequest;
import com.lms.operation.dto.request.VerifyGuarantorRequest;
import com.lms.operation.dto.response.GuarantorResponse;
import com.lms.operation.exception.AppException;
import com.lms.operation.exception.ErrorCode;
import com.lms.operation.repository.GuarantorRepository;
import com.lms.operation.repository.LoanApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GuarantorServiceImpl implements GuarantorService {

    private final GuarantorRepository guarantorRepository;
    private final LoanApplicationRepository loanApplicationRepository;

    @Override
    @Transactional
    public GuarantorResponse createGuarantor(Integer applicationId, CreateGuarantorRequest request) {
        LoanApplication application = loanApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new AppException(ErrorCode.APPLICATION_NOT_FOUND));

        Guarantor guarantor = new Guarantor();
        guarantor.setApplication(application);
        guarantor.setFirstName(request.getFirstName());
        guarantor.setLastName(request.getLastName());
        guarantor.setDateOfBirth(request.getDateOfBirth());
        guarantor.setPhoneNumber(request.getPhoneNumber());
        guarantor.setEmail(request.getEmail());
        guarantor.setNationalId(request.getNationalId());
        guarantor.setAddress(request.getAddress());
        guarantor.setOccupation(request.getOccupation());
        guarantor.setEmployer(request.getEmployer());
        guarantor.setMonthlyIncome(request.getMonthlyIncome());
        guarantor.setRelationshipToCustomer(request.getRelationshipToCustomer());
        guarantor.setVerificationStatus("Pending");

        Guarantor saved = guarantorRepository.save(guarantor);
        return GuarantorResponse.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GuarantorResponse> getGuarantors(Integer applicationId) {
        return guarantorRepository.findByApplication_ApplicationId(applicationId).stream()
                .map(GuarantorResponse::from)
                .toList();
    }

    @Override
    @Transactional
    public GuarantorResponse verifyGuarantor(Integer applicationId, Integer guarantorId, VerifyGuarantorRequest request) {
        Guarantor guarantor = guarantorRepository.findById(guarantorId)
                .orElseThrow(() -> new AppException(ErrorCode.GUARANTOR_NOT_FOUND));

        if (guarantor.getApplication() == null
                || !guarantor.getApplication().getApplicationId().equals(applicationId)) {
            throw new AppException(ErrorCode.GUARANTOR_NOT_FOUND);
        }

        guarantor.setVerificationStatus("Verified");
        guarantor.setVerifiedBy(getCurrentUserId());
        guarantor.setVerifiedAt(LocalDateTime.now());
        // NOTE: request.getVerificationComment() is accepted but the Guarantor entity
        // has no verificationComment field. Add a column to persist it if required.

        Guarantor saved = guarantorRepository.save(guarantor);
        return GuarantorResponse.from(saved);
    }

    private Integer getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof UsernamePasswordAuthenticationToken token) {
            Object details = token.getDetails();
            if (details instanceof Integer userId) {
                return userId;
            }
        }
        return null;
    }
}