package com.lms.operation.service;

import com.lms.operation.domain.DisbursementTransaction;
import com.lms.operation.domain.Loan;
import com.lms.operation.domain.LoanPayment;
import com.lms.operation.domain.LoanProduct;
import com.lms.operation.domain.LoanRequest;
import com.lms.operation.dto.request.DisburseRequest;
import com.lms.operation.dto.response.DisburseResponse;
import com.lms.operation.exception.AppException;
import com.lms.operation.exception.ErrorCode;
import com.lms.operation.repository.DisbursementTransactionRepository;
import com.lms.operation.repository.LoanPaymentRepository;
import com.lms.operation.repository.LoanRepository;
import com.lms.operation.repository.LoanRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DisburseServiceImpl implements DisburseService {

    private static final String PAYMENT_METHOD_CASH = "CASH";
    private static final String PAYMENT_METHOD_BANK_TRANSFER = "BANK_TRANSFER";

    private final LoanRequestRepository loanRequestRepository;
    private final LoanRepository loanRepository;
    private final LoanPaymentRepository loanPaymentRepository;
    private final DisbursementTransactionRepository disbursementTransactionRepository;

    @Override
    @Transactional
    public DisburseResponse disburse(Integer loanRequestId, DisburseRequest request) {

        // 1. Load loan request
        LoanRequest loanRequest = loanRequestRepository.findById(loanRequestId)
                .orElseThrow(() -> new AppException(ErrorCode.LOAN_REQUEST_NOT_FOUND));

        // 2. Must be APPROVED
        if (!"APPROVED".equals(loanRequest.getStatus())) {
            throw new AppException(ErrorCode.LOAN_REQUEST_NOT_APPROVED);
        }

        // 3. Must not already be disbursed (no loan already created for this request)
        if (loanRepository.existsByLoanRequestId(loanRequest.getLoanRequestId())) {
            throw new AppException(ErrorCode.LOAN_ALREADY_DISBURSED);
        }

        // Payment method validation
        String paymentMethod = request.getPaymentMethod() == null
                ? "" : request.getPaymentMethod().trim();
        if (!PAYMENT_METHOD_CASH.equals(paymentMethod)
                && !PAYMENT_METHOD_BANK_TRANSFER.equals(paymentMethod)) {
            throw new AppException(ErrorCode.VALIDATION_FAILED,
                    "Payment method must be CASH or BANK_TRANSFER");
        }

        // Bank transfer requires reference + account, and reference must be unique
        if (PAYMENT_METHOD_BANK_TRANSFER.equals(paymentMethod)) {
            if (request.getBankReference() == null || request.getBankReference().isBlank()) {
                throw new AppException(ErrorCode.VALIDATION_FAILED,
                        "Bank reference is required for BANK_TRANSFER");
            }
            if (request.getAccountId() == null) {
                throw new AppException(ErrorCode.VALIDATION_FAILED,
                        "Bank account id is required for BANK_TRANSFER");
            }
            if (disbursementTransactionRepository.existsByBankReference(request.getBankReference())) {
                throw new AppException(ErrorCode.BANK_REFERENCE_DUPLICATE);
            }
        }

        LoanProduct product = loanRequest.getLoanProduct();
        BigDecimal principal = loanRequest.getPrincipalAmount();
        BigDecimal interestRate = product.getInterestRate();
        int termMonths = loanRequest.getDuration();
        LocalDate startDate = request.getDisbursementDate();

        // 4. Create the loan
        Loan loan = Loan.builder()
                .loanNumber(generateLoanNumber())
                .approvedAmount(principal)
                .interestRate(interestRate)
                .loanTermMonths(termMonths)
                .monthlyPayment(loanRequest.getMonthlyPaymentAmount())
                .outstandingBalance(principal)
                .loanStatus("Active")
                .startDate(startDate)
                .endDate(startDate.plusMonths(termMonths))
                .currency(loanRequest.getCurrency())
                .serviceFeeAmount(loanRequest.getServiceFeeAmount())
                .repaymentFrequency(loanRequest.getRepaymentFrequency())
                .loanRequestId(loanRequest.getLoanRequestId())
                .employeeId(loanRequest.getEmployeeId())
                .branchId(loanRequest.getBranchId())
                .build();
        loanRepository.save(loan);

        // 5. Generate the amortization schedule
        List<LoanPayment> schedule = buildSchedule(loan, principal, interestRate, termMonths, startDate);
        loanPaymentRepository.saveAll(schedule);

        // 6. Record the disbursement transaction
        DisbursementTransaction transaction = DisbursementTransaction.builder()
                .loanId(loan.getLoanId())
                .loanRequestId(loanRequest.getLoanRequestId())
                .disbursementDate(request.getDisbursementDate())
                .paymentMethod(paymentMethod)
                .bankReference(request.getBankReference())
                .bankAccountId(request.getAccountId())
                .amount(principal)
                .currency(loanRequest.getCurrency())
                .notes(request.getNotes())
                .disbursedBy(getCurrentUserId())
                .status("Completed")
                .build();
        disbursementTransactionRepository.save(transaction);

        // 7. Mark the request as disbursed
        loanRequest.setStatus("DISBURSED");
        loanRequestRepository.save(loanRequest);

        log.info("Loan request {} disbursed as loan {} ({} schedule rows)",
                loanRequest.getLoanRequestNumber(), loan.getLoanNumber(), schedule.size());

        return new DisburseResponse(
                loan.getLoanId(),
                loan.getLoanNumber(),
                loan.getLoanStatus(),
                principal,
                loanRequest.getCurrency(),
                termMonths,
                loanRequest.getRepaymentFrequency(),
                transaction.getCreatedAt(),
                schedule.size(),
                schedule.get(0).getDueDate(),
                schedule.get(schedule.size() - 1).getDueDate()
        );
    }

    private List<LoanPayment> buildSchedule(Loan loan, BigDecimal principal,
                                            BigDecimal interestRate, int termMonths,
                                            LocalDate startDate) {

        double monthlyRate = interestRate == null
                ? 0.0 : interestRate.doubleValue() / 100.0 / 12.0;

        BigDecimal monthlyPayment;
        if (monthlyRate == 0.0) {
            monthlyPayment = principal.divide(BigDecimal.valueOf(termMonths), 2, RoundingMode.HALF_UP);
        } else {
            double pow = Math.pow(1 + monthlyRate, termMonths);
            double mp = principal.doubleValue() * monthlyRate * pow / (pow - 1);
            monthlyPayment = BigDecimal.valueOf(mp).setScale(2, RoundingMode.HALF_UP);
        }

        List<LoanPayment> schedule = new ArrayList<>();
        BigDecimal balance = principal;

        for (int i = 1; i <= termMonths; i++) {
            BigDecimal interest = balance
                    .multiply(BigDecimal.valueOf(monthlyRate))
                    .setScale(2, RoundingMode.HALF_UP);
            BigDecimal principalPortion = monthlyPayment.subtract(interest);
            balance = balance.subtract(principalPortion);

            // zero-out any rounding residue on the final installment
            BigDecimal remaining = (i == termMonths)
                    ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
                    : balance.max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);

            LoanPayment payment = LoanPayment.builder()
                    .loan(loan)
                    .paymentNo(i)
                    .dueDate(startDate.plusMonths(i))
                    .amountDue(monthlyPayment)
                    .amountPaid(BigDecimal.ZERO)
                    .remainingBalance(remaining)
                    .paymentStatus("Pending")
                    .build();
            schedule.add(payment);
        }
        return schedule;
    }

    private String generateLoanNumber() {
        return "LN-" + LocalDate.now().getYear() + "-"
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
}
