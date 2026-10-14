package com.lms.operation.service;

import com.lms.operation.domain.Customer;
import com.lms.operation.domain.LoanProduct;
import com.lms.operation.domain.LoanRequest;
import com.lms.operation.dto.request.CreateLoanRequestRequest;
import com.lms.operation.dto.request.LoanRequestDecisionRequest;
import com.lms.operation.dto.response.LoanRequestDecisionResponse;
import com.lms.operation.dto.response.LoanRequestResponse;
import com.lms.operation.exception.AppException;
import com.lms.operation.exception.ErrorCode;
import com.lms.operation.repository.CustomerRepository;
import com.lms.operation.repository.LoanProductRepository;
import com.lms.operation.repository.LoanRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanRequestServiceImpl implements LoanRequestService {

    private final LoanRequestRepository loanRequestRepository;
    private final CustomerRepository customerRepository;
    private final LoanProductRepository loanProductRepository;

    @Override
    @Transactional
    public LoanRequestResponse createLoanRequest(CreateLoanRequestRequest request) {

        // Validate customer exists and is active
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new AppException(ErrorCode.CUSTOMER_NOT_FOUND));
        if (!"Active".equalsIgnoreCase(customer.getStatus())) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "Customer is not active");
        }

        // Validate loan product exists and is active
        LoanProduct product = loanProductRepository.findById(request.getLoanProductId())
                .orElseThrow(() -> new AppException(ErrorCode.LOAN_PRODUCT_NOT_FOUND));
        if (!"Active".equalsIgnoreCase(product.getStatus())) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "Loan product is not active");
        }

        // Validate principal amount does not exceed product limit
        if (product.getMaxLoanAmount() != null
                && request.getPrincipalAmount().compareTo(product.getMaxLoanAmount()) > 0) {
            throw new AppException(ErrorCode.APPLICATION_AMOUNT_EXCEEDS_LIMIT);
        }

        // Validate duration is within the product term range
        if (product.getMinimumTermMonths() != null
                && product.getMaximumTermMonths() != null
                && (request.getDuration() < product.getMinimumTermMonths()
                || request.getDuration() > product.getMaximumTermMonths())) {
            throw new AppException(ErrorCode.VALIDATION_FAILED,
                    "Duration must be between " + product.getMinimumTermMonths()
                            + " and " + product.getMaximumTermMonths() + " months");
        }

        LoanRequest loanRequest = LoanRequest.builder()
                .loanRequestNumber(generateLoanRequestNumber())
                .customer(customer)
                .loanProduct(product)
                .employeeId(request.getEmployeeId())
                .branchId(request.getBranchId())
                .principalAmount(request.getPrincipalAmount())
                .currency(request.getCurrency())
                .duration(request.getDuration())
                .durationUnit(request.getDurationUnit())
                .repaymentFrequency(request.getRepaymentFrequency())
                .monthlyPaymentAmount(request.getMonthlyPaymentAmount())
                .serviceFeeAmount(request.getServiceFeeAmount())
                .purpose(request.getPurpose())
                .requestedDisbursementDate(request.getRequestedDisbursementDate())
                .notes(request.getNotes())
                .status("PENDING")
                .createdBy(getCurrentUserId())
                .build();

        loanRequestRepository.save(loanRequest);
        log.info("Loan request created: {}", loanRequest.getLoanRequestNumber());
        return toResponse(loanRequest);
    }

    @Override
    @Transactional
    public LoanRequestDecisionResponse decideRequest(Integer loanRequestId,
                                                     LoanRequestDecisionRequest request) {

        LoanRequest loanRequest = loanRequestRepository.findById(loanRequestId)
                .orElseThrow(() -> new AppException(ErrorCode.LOAN_REQUEST_NOT_FOUND));

        // Decision must be exactly APPROVE or REJECT
        String decision = request.getDecision() == null ? "" : request.getDecision().trim();
        boolean approve = "APPROVE".equals(decision);
        boolean reject = "REJECT".equals(decision);
        if (!approve && !reject) {
            throw new AppException(ErrorCode.VALIDATION_FAILED,
                    "Decision must be APPROVE or REJECT");
        }

        // Comment is required when rejecting
        if (reject && (request.getComment() == null || request.getComment().isBlank())) {
            throw new AppException(ErrorCode.VALIDATION_FAILED,
                    "Comment is required when rejecting a loan request");
        }

        // Only PENDING or IN_REVIEW requests can be decided
        String status = loanRequest.getStatus();
        if (!"PENDING".equals(status) && !"IN_REVIEW".equals(status)) {
            throw new AppException(ErrorCode.LOAN_REQUEST_ALREADY_DECIDED);
        }

        // Approver cannot be the creator
        Integer reviewedBy = getCurrentUserId();
        if (reviewedBy != null && reviewedBy.equals(loanRequest.getCreatedBy())) {
            throw new AppException(ErrorCode.ACCESS_DENIED,
                    "Approver cannot be the creator of the loan request");
        }

        loanRequest.setReviewedBy(reviewedBy);
        loanRequest.setReviewedAt(LocalDateTime.now());
        loanRequest.setReviewComment(request.getComment());
        loanRequest.setStatus(approve ? "APPROVED" : "REJECTED");

        loanRequestRepository.save(loanRequest);
        log.info("Loan request {} decided: {} by user {}",
                loanRequest.getLoanRequestNumber(), loanRequest.getStatus(), reviewedBy);
        return toDecisionResponse(loanRequest);
    }

    private String generateLoanRequestNumber() {
        return "LR-" + LocalDate.now().getYear() + "-"
                + String.format("%04d", System.currentTimeMillis() % 10000);
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

    private LoanRequestResponse toResponse(LoanRequest r) {
        String customerName = r.getCustomer().getFirstName()
                + " " + r.getCustomer().getLastName();
        String productName = r.getLoanProduct().getProductName();

        return new LoanRequestResponse(
                r.getLoanRequestId(),
                r.getLoanRequestNumber(),
                r.getCustomer().getCustomerId(),
                customerName,
                r.getLoanProduct().getLoanProductId(),
                productName,
                r.getEmployeeId(),
                r.getBranchId(),
                r.getPrincipalAmount(),
                r.getCurrency(),
                r.getDuration(),
                r.getDurationUnit(),
                r.getRepaymentFrequency(),
                r.getMonthlyPaymentAmount(),
                r.getServiceFeeAmount(),
                r.getPurpose(),
                r.getRequestedDisbursementDate(),
                r.getNotes(),
                r.getStatus(),
                r.getCreatedBy(),
                r.getCreatedAt()
        );
    }

    private LoanRequestDecisionResponse toDecisionResponse(LoanRequest r) {
        String customerName = r.getCustomer().getFirstName()
                + " " + r.getCustomer().getLastName();

        return new LoanRequestDecisionResponse(
                r.getLoanRequestId(),
                r.getLoanRequestNumber(),
                r.getStatus(),
                r.getReviewedBy(),
                r.getReviewedAt(),
                r.getReviewComment(),
                r.getCustomer().getCustomerId(),
                customerName,
                r.getPrincipalAmount(),
                r.getCurrency()
        );
    }
}
