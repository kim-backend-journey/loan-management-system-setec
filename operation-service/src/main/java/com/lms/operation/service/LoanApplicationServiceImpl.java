package com.lms.operation.service;

import com.lms.operation.domain.*;
import com.lms.operation.dto.request.ApplicationStatusRequest;
import com.lms.operation.dto.request.CreateApplicationRequest;
import com.lms.operation.dto.request.UpdateApplicationRequest;
import com.lms.operation.dto.response.ApplicationResponse;
import com.lms.operation.dto.response.ApplicationStatusHistoryResponse;
import com.lms.operation.exception.AppException;
import com.lms.operation.exception.ErrorCode;
import com.lms.operation.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanApplicationServiceImpl implements LoanApplicationService {

    private final LoanApplicationRepository loanApplicationRepository;
    private final ApplicationStatusHistoryRepository statusHistoryRepository;
    private final CustomerRepository customerRepository;
    private final LoanProductRepository loanProductRepository;

    @Override
    @Transactional
    public ApplicationResponse createApplication(CreateApplicationRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new AppException(ErrorCode.CUSTOMER_NOT_FOUND));

        LoanProduct product = loanProductRepository.findById(request.getLoanProductId())
                .orElseThrow(() -> new AppException(ErrorCode.LOAN_PRODUCT_NOT_FOUND));

        // Validate amount does not exceed product limit
        if (request.getRequestedAmount()
                .compareTo(product.getMaxLoanAmount()) > 0) {
            throw new AppException(ErrorCode.APPLICATION_AMOUNT_EXCEEDS_LIMIT);
        }

        LoanApplication application = LoanApplication.builder()
                .applicationNumber("APP-" + System.currentTimeMillis())
                .customer(customer)
                .loanProduct(product)
                .requestedAmount(request.getRequestedAmount())
                .requestedTermMonths(request.getRequestedTermMonths())
                .loanPurpose(request.getLoanPurpose())
                .applicationStatus("Draft")
                .build();

        loanApplicationRepository.save(application);
        log.info("Application created: {}", application.getApplicationNumber());
        return toResponse(application);
    }

    @Override
    public List<ApplicationResponse> getAllApplications() {
        return loanApplicationRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public ApplicationResponse getApplicationById(Integer id) {
        return toResponse(findApplication(id));
    }

    @Override
    @Transactional
    public ApplicationResponse submitApplication(Integer id) {
        LoanApplication application = findApplication(id);

        if (!"Draft".equals(application.getApplicationStatus())) {
            throw new AppException(ErrorCode.INVALID_APPLICATION_STATUS,
                    "Only Draft applications can be submitted");
        }

        String oldStatus = application.getApplicationStatus();
        application.setApplicationStatus("Submitted");
        application.setSubmittedAt(LocalDateTime.now());
        loanApplicationRepository.save(application);

        saveStatusHistory(application, oldStatus, "Submitted",
                "Application submitted");
        log.info("Application submitted: {}", id);
        return toResponse(application);
    }

    @Override
    @Transactional
    public ApplicationResponse updateApplication(Integer id,
                                                 UpdateApplicationRequest request) {
        LoanApplication application = findApplication(id);

        if (!"Draft".equals(application.getApplicationStatus())) {
            throw new AppException(ErrorCode.INVALID_APPLICATION_STATUS,
                    "Only Draft applications can be updated");
        }

        if (request.getRequestedAmount() != null) {
            if (request.getRequestedAmount()
                    .compareTo(application.getLoanProduct()
                            .getMaxLoanAmount()) > 0) {
                throw new AppException(ErrorCode.APPLICATION_AMOUNT_EXCEEDS_LIMIT);
            }
            application.setRequestedAmount(request.getRequestedAmount());
        }
        if (request.getRequestedTermMonths() != null)
            application.setRequestedTermMonths(request.getRequestedTermMonths());
        if (request.getLoanPurpose() != null)
            application.setLoanPurpose(request.getLoanPurpose());

        loanApplicationRepository.save(application);
        return toResponse(application);
    }

    @Override
    @Transactional
    public ApplicationResponse updateApplicationStatus(Integer id,
                                                       ApplicationStatusRequest request) {
        LoanApplication application = findApplication(id);
        String oldStatus = application.getApplicationStatus();

        application.setApplicationStatus(request.getStatus());
        loanApplicationRepository.save(application);

        saveStatusHistory(application, oldStatus,
                request.getStatus(), request.getRemark());
        log.info("Application {} status changed: {} → {}",
                id, oldStatus, request.getStatus());
        return toResponse(application);
    }

    @Override
    public List<ApplicationStatusHistoryResponse> getApplicationHistory(Integer id) {
        findApplication(id);
        return statusHistoryRepository
                .findByApplication_ApplicationId(id)
                .stream()
                .map(this::toHistoryResponse)
                .toList();
    }

    private LoanApplication findApplication(Integer id) {
        return loanApplicationRepository.findById(id)
                .orElseThrow(() -> new AppException(
                        ErrorCode.APPLICATION_NOT_FOUND));
    }

    private Integer getCurrentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof UsernamePasswordAuthenticationToken token) {
            Object details = token.getDetails();
            if (details instanceof Integer userId) {
                return userId;
            }
        }
        return 3; // fallback to admin
    }

    private void saveStatusHistory(LoanApplication application,
                                   String oldStatus, String newStatus,
                                   String remark) {

        ApplicationStatusHistory history = ApplicationStatusHistory.builder()
                .application(application)
                .changedBy(getCurrentUserId()) // ← real user ID now
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .remark(remark)
                .build();


        statusHistoryRepository.save(history);
    }

    private ApplicationResponse toResponse(LoanApplication a) {
        String customerName = a.getCustomer().getFirstName()
                + " " + a.getCustomer().getLastName();
        String productName = a.getLoanProduct().getProductName();

        return new ApplicationResponse(
                a.getApplicationId(),
                a.getApplicationNumber(),
                a.getCustomer().getCustomerId(),
                customerName,
                a.getLoanProduct().getLoanProductId(),
                productName,
                a.getRequestedAmount(),
                a.getRequestedTermMonths(),
                a.getLoanPurpose(),
                a.getApplicationStatus(),
                a.getSubmittedAt(),
                a.getCreatedAt(),
                a.getUpdatedAt()
        );
    }

    private ApplicationStatusHistoryResponse toHistoryResponse(
            ApplicationStatusHistory h) {
        return new ApplicationStatusHistoryResponse(
                h.getHistoryId(),
                h.getApplication().getApplicationId(),
                h.getChangedBy(),
                h.getOldStatus(),
                h.getNewStatus(),
                h.getRemark(),
                h.getCreatedAt()
        );
    }
}