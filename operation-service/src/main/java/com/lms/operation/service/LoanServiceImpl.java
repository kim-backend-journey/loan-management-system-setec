package com.lms.operation.service;

import com.lms.operation.domain.Loan;
import com.lms.operation.domain.LoanApplication;
import com.lms.operation.domain.LoanPayment;
import com.lms.operation.dto.response.LoanResponse;
import com.lms.operation.exception.AppException;
import com.lms.operation.exception.ErrorCode;
import com.lms.operation.repository.LoanApplicationRepository;
import com.lms.operation.repository.LoanPaymentRepository;
import com.lms.operation.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;
    private final LoanPaymentRepository loanPaymentRepository;
    private final LoanApplicationRepository loanApplicationRepository;

    @Override
    @Transactional
    public LoanResponse disburse(Integer applicationId) {
        LoanApplication application = loanApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new AppException(ErrorCode.APPLICATION_NOT_FOUND));

        // Already disbursed?
        loanRepository.findByApplication_ApplicationId(applicationId).ifPresent(l -> {
            throw new AppException(ErrorCode.LOAN_ALREADY_DISBURSED);
        });

        // Must be approved
        if (!"Approved".equalsIgnoreCase(application.getApplicationStatus())) {
            throw new AppException(ErrorCode.APPLICATION_NOT_APPROVED);
        }

        LocalDate startDate = LocalDate.now();

        BigDecimal approvedAmount = application.getRequestedAmount();
        BigDecimal interestRate = application.getLoanProduct().getInterestRate();
        int n = application.getRequestedTermMonths();

        // monthlyPayment = P * r * (1+r)^n / ((1+r)^n - 1)
        double monthlyRate = interestRate.doubleValue() / 100.0 / 12.0;
        double principal = approvedAmount.doubleValue();
        double monthlyPaymentValue;
        if (monthlyRate == 0.0) {
            monthlyPaymentValue = principal / n;
        } else {
            double factor = Math.pow(1 + monthlyRate, n);
            monthlyPaymentValue = principal * monthlyRate * factor / (factor - 1);
        }
        BigDecimal monthlyPayment = BigDecimal.valueOf(monthlyPaymentValue).setScale(2, RoundingMode.HALF_UP);

        Loan loan = new Loan();
        loan.setApplication(application);
        loan.setLoanNumber("LN-" + System.currentTimeMillis());
        loan.setApprovedAmount(approvedAmount);
        loan.setInterestRate(interestRate);
        loan.setLoanTermMonths(n);
        loan.setMonthlyPayment(monthlyPayment);
        loan.setOutstandingBalance(approvedAmount);
        loan.setLoanStatus("ACTIVE");
        loan.setStartDate(startDate);
        loan.setEndDate(startDate.plusMonths(n));
        loan = loanRepository.save(loan);

        // Payment schedule (amortized)
        BigDecimal monthlyRateBd = BigDecimal.valueOf(monthlyRate);
        BigDecimal balance = approvedAmount;
        List<LoanPayment> payments = new ArrayList<>();

        for (int i = 1; i <= n; i++) {
            BigDecimal interest = balance.multiply(monthlyRateBd).setScale(2, RoundingMode.HALF_UP);
            BigDecimal principalPortion = monthlyPayment.subtract(interest);
            BigDecimal amountDue = monthlyPayment;
            BigDecimal remaining;

            if (i == n) {
                // Final payment clears the remaining balance (handles rounding drift)
                amountDue = balance.add(interest).setScale(2, RoundingMode.HALF_UP);
                remaining = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
            } else {
                remaining = balance.subtract(principalPortion).setScale(2, RoundingMode.HALF_UP);
            }

            LoanPayment payment = new LoanPayment();
            payment.setLoan(loan);
            payment.setPaymentNo(i);
            payment.setDueDate(startDate.plusMonths(i));
            payment.setAmountDue(amountDue);
            payment.setAmountPaid(BigDecimal.ZERO);
            payment.setRemainingBalance(remaining);
            payment.setPaymentStatus("PENDING");
            payment.setLateFee(BigDecimal.ZERO);
            payment.setPenaltyAmount(BigDecimal.ZERO);
            payments.add(payment);

            balance = remaining;
        }
        loanPaymentRepository.saveAll(payments);

        application.setApplicationStatus("Disbursed");
        loanApplicationRepository.save(application);

        return toResponse(loan);
    }

    @Override
    public LoanResponse getLoan(Integer loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new AppException(ErrorCode.LOAN_NOT_FOUND));
        return toResponse(loan);
    }

    private LoanResponse toResponse(Loan l) {
        return new LoanResponse(
                l.getLoanId(),
                l.getApplication().getApplicationId(),
                l.getLoanNumber(),
                l.getApprovedAmount(),
                l.getInterestRate(),
                l.getLoanTermMonths(),
                l.getMonthlyPayment(),
                l.getOutstandingBalance(),
                l.getLoanStatus(),
                l.getStartDate(),
                l.getEndDate(),
                l.getCreatedAt(),
                l.getUpdatedAt()
        );
    }
}