package com.lms.operation.service;

import com.lms.operation.exception.AppException;
import com.lms.operation.exception.ErrorCode;
import com.lms.operation.dto.request.CreateLoanProductRequest;
import com.lms.operation.dto.request.LoanProductStatusRequest;
import com.lms.operation.dto.request.UpdateLoanProductRequest;
import com.lms.operation.dto.response.LoanProductResponse;
import com.lms.operation.domain.LoanProduct;
import com.lms.operation.repository.LoanProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanProductServiceImpl implements LoanProductService {

    private final LoanProductRepository loanProductRepository;

    @Override
    @Transactional
    public LoanProductResponse createLoanProduct(CreateLoanProductRequest request) {
        log.info("Creating loan product: {}", request.getProductName());

        LoanProduct loanProduct = LoanProduct.builder()
                .productName(request.getProductName())
                .maxLoanAmount(request.getMaxLoanAmount())
                .minimumIncome(request.getMinimumIncome())
                .interestRate(request.getInterestRate())
                .minimumTermMonths(request.getMinimumTermMonths())
                .maximumTermMonths(request.getMaximumTermMonths())
                .processingFee(request.getProcessingFee())
                .description(request.getDescription())
                .status("Active")
                .build();

        loanProductRepository.save(loanProduct);
        log.info("Loan product created with id: {}", loanProduct.getLoanProductId());
        return toResponse(loanProduct);
    }

    @Override
    public List<LoanProductResponse> getAllLoanProducts() {
        return loanProductRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public LoanProductResponse getLoanProductById(Integer id) {
        return toResponse(findLoanProductOrThrow(id));
    }

    @Override
    @Transactional
    public LoanProductResponse updateLoanProduct(Integer id,
                                                 UpdateLoanProductRequest request) {
        LoanProduct loanProduct = findLoanProductOrThrow(id);

        if (request.getProductName() != null)
            loanProduct.setProductName(request.getProductName());
        if (request.getMaxLoanAmount() != null)
            loanProduct.setMaxLoanAmount(request.getMaxLoanAmount());
        if (request.getMinimumIncome() != null)
            loanProduct.setMinimumIncome(request.getMinimumIncome());
        if (request.getInterestRate() != null)
            loanProduct.setInterestRate(request.getInterestRate());
        if (request.getMinimumTermMonths() != null)
            loanProduct.setMinimumTermMonths(request.getMinimumTermMonths());
        if (request.getMaximumTermMonths() != null)
            loanProduct.setMaximumTermMonths(request.getMaximumTermMonths());
        if (request.getProcessingFee() != null)
            loanProduct.setProcessingFee(request.getProcessingFee());
        if (request.getDescription() != null)
            loanProduct.setDescription(request.getDescription());

        loanProductRepository.save(loanProduct);
        log.info("Loan product updated: {}", id);
        return toResponse(loanProduct);
    }

    @Override
    @Transactional
    public LoanProductResponse updateLoanProductStatus(Integer id,
                                                       LoanProductStatusRequest request) {
        LoanProduct loanProduct = findLoanProductOrThrow(id);
        loanProduct.setStatus(request.getStatus());
        loanProductRepository.save(loanProduct);
        log.info("Loan product {} status updated to {}", id, request.getStatus());
        return toResponse(loanProduct);
    }

    private LoanProduct findLoanProductOrThrow(Integer id) {
        return loanProductRepository.findById(id)
                .orElseThrow(() -> new AppException(
                        ErrorCode.LOAN_PRODUCT_NOT_FOUND));
    }

    private LoanProductResponse toResponse(LoanProduct p) {
        return new LoanProductResponse(
                p.getLoanProductId(),
                p.getProductName(),
                p.getMaxLoanAmount(),
                p.getMinimumIncome(),
                p.getInterestRate(),
                p.getMinimumTermMonths(),
                p.getMaximumTermMonths(),
                p.getProcessingFee(),
                p.getDescription(),
                p.getStatus(),
                p.getReviewedBy(),
                p.getReviewedAt(),
                p.getCreatedAt(),
                p.getUpdatedAt()
        );
    }
}