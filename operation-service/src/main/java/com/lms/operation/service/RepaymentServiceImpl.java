package com.lms.operation.service;

import com.lms.operation.domain.CollectionLedger;
import com.lms.operation.domain.Employee;
import com.lms.operation.domain.Loan;
import com.lms.operation.domain.LoanPayment;
import com.lms.operation.domain.PaymentTransaction;
import com.lms.operation.dto.request.RepaymentRequest;
import com.lms.operation.dto.response.AllocationResponse;
import com.lms.operation.dto.response.RepaymentResponse;
import com.lms.operation.exception.AppException;
import com.lms.operation.exception.ErrorCode;
import com.lms.operation.repository.CollectionLedgerRepository;
import com.lms.operation.repository.EmployeeRepository;
import com.lms.operation.repository.LoanPaymentRepository;
import com.lms.operation.repository.LoanRepository;
import com.lms.operation.repository.PaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RepaymentServiceImpl implements RepaymentService {

    private static final String PAYMENT_METHOD_CASH = "CASH";
    private static final String PAYMENT_METHOD_BANK_TRANSFER = "BANK_TRANSFER";

    private final LoanPaymentRepository loanPaymentRepository;
    private final LoanRepository loanRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final CollectionLedgerRepository collectionLedgerRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional
    public RepaymentResult createRepayment(RepaymentRequest request) {

        // 1. Idempotency — replay returns the original response (HTTP 200)
        if (request.getIdempotencyKey() != null && !request.getIdempotencyKey().isBlank()) {
            var existing = paymentTransactionRepository
                    .findByIdempotencyKey(request.getIdempotencyKey());
            if (existing.isPresent()) {
                log.info("Idempotent replay for key {}", request.getIdempotencyKey());
                return new RepaymentResult(buildReplayResponse(existing.get()), true);
            }
        }

        // 2. Load and validate the schedule (loan payment)
        LoanPayment payment = loanPaymentRepository.findById(request.getScheduleId())
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));

        Loan loan = payment.getLoan();
        if (loan == null || !loan.getLoanId().equals(request.getLoanId())) {
            throw new AppException(ErrorCode.VALIDATION_FAILED,
                    "Loan id does not match the schedule's loan");
        }

        if ("Paid".equalsIgnoreCase(payment.getPaymentStatus())) {
            throw new AppException(ErrorCode.PAYMENT_ALREADY_PAID);
        }

        // Payment method + bank transfer validation
        String paymentMethod = request.getPaymentMethod() == null
                ? "" : request.getPaymentMethod().trim();
        boolean bankTransfer = PAYMENT_METHOD_BANK_TRANSFER.equals(paymentMethod);
        if (!PAYMENT_METHOD_CASH.equals(paymentMethod) && !bankTransfer) {
            throw new AppException(ErrorCode.VALIDATION_FAILED,
                    "Payment method must be CASH or BANK_TRANSFER");
        }
        if (bankTransfer) {
            if (request.getBankReference() == null || request.getBankReference().isBlank()) {
                throw new AppException(ErrorCode.VALIDATION_FAILED,
                        "Bank reference is required for BANK_TRANSFER");
            }
            if (request.getBankAccountId() == null) {
                throw new AppException(ErrorCode.VALIDATION_FAILED,
                        "Bank account id is required for BANK_TRANSFER");
            }
        }
        if (request.getBankReference() != null && !request.getBankReference().isBlank()
                && paymentTransactionRepository.existsByBankReference(request.getBankReference())) {
            throw new AppException(ErrorCode.BANK_REFERENCE_DUPLICATE);
        }
        if (request.getIdempotencyKey() != null && !request.getIdempotencyKey().isBlank()
                && paymentTransactionRepository.existsByIdempotencyKey(request.getIdempotencyKey())) {
            throw new AppException(ErrorCode.IDEMPOTENCY_KEY_DUPLICATE);
        }

        // 3. Allocation: Penalty -> ServiceFee -> Interest -> Principal
        BigDecimal totalAmount = request.getTotalAmount();

        BigDecimal penalty = nz(payment.getPenaltyAmount());
        BigDecimal penaltyAllocation = totalAmount.min(penalty);
        BigDecimal remaining = totalAmount.subtract(penaltyAllocation);

        // LoanPayment has no per-schedule service fee -> allocation is 0
        BigDecimal serviceFeeAllocation = remaining.min(BigDecimal.ZERO);
        remaining = remaining.subtract(serviceFeeAllocation);

        BigDecimal interestRate = nz(loan.getInterestRate());
        BigDecimal monthlyRate = interestRate.divide(BigDecimal.valueOf(1200), 10, RoundingMode.HALF_UP);
        BigDecimal interestAllocation = nz(payment.getAmountDue())
                .multiply(monthlyRate)
                .setScale(2, RoundingMode.HALF_UP);
        // never allocate more interest than what is left
        interestAllocation = interestAllocation.min(remaining);
        BigDecimal principalAllocation = remaining.subtract(interestAllocation);

        // 4. Update the schedule
        boolean fullyPaid = totalAmount.compareTo(nz(payment.getAmountDue())) >= 0;
        payment.setAmountPaid(totalAmount);
        payment.setPaymentDate(request.getPaidAt());
        payment.setPaymentStatus(fullyPaid ? "Paid" : "Partial");
        payment.setPaidBy("Cashier");
        loanPaymentRepository.save(payment);

        // 5. Reduce the loan outstanding principal
        loan.setOutstandingBalance(nz(loan.getOutstandingBalance()).subtract(principalAllocation));

        // 6. Receipt number
        String receiptNumber = generateReceiptNumber();

        // 7. Payment transaction
        String bankVerificationStatus = bankTransfer ? "PENDING" : "N/A";
        String referenceNumber = (request.getBankReference() != null && !request.getBankReference().isBlank())
                ? request.getBankReference()
                : "TXN-" + System.currentTimeMillis();

        PaymentTransaction transaction = PaymentTransaction.builder()
                .payment(payment)
                .paymentMethod(paymentMethod)
                .amount(totalAmount)
                .referenceNumber(referenceNumber)
                .bankReference(request.getBankReference())
                .bankAccountId(request.getBankAccountId())
                .bankVerificationStatus(bankVerificationStatus)
                .transactionStatus("Success")
                .receivedBy(getCurrentUserId())
                .transactionDate(request.getPaidAt())
                .idempotencyKey(request.getIdempotencyKey())
                .receiptNumber(receiptNumber)
                .currency(request.getCurrency())
                .notes(request.getNotes())
                .isVoided(false)
                .build();
        paymentTransactionRepository.save(transaction);

        // 8. Collection ledger
        Employee employee = resolveEmployee(request.getReceivedByEmployeeId());
        Integer ledgerEmployeeId = employee != null
                ? employee.getEmployeeId() : request.getReceivedByEmployeeId();
        Integer ledgerBranchId = employee != null ? employee.getBranchId() : null;

        CollectionLedger ledger = CollectionLedger.builder()
                .employeeId(ledgerEmployeeId)
                .branchId(ledgerBranchId)
                .businessDate(request.getPaidAt().toLocalDate())
                .paymentId(payment.getPaymentId())
                .transactionId(transaction.getTransactionId())
                .paymentMethod(paymentMethod)
                .principalAmount(principalAllocation)
                .interestAmount(interestAllocation)
                .serviceFeeAmount(serviceFeeAllocation)
                .penaltyAmount(penaltyAllocation)
                .totalAmount(totalAmount)
                .currency(request.getCurrency())
                .isVoided(false)
                .build();
        collectionLedgerRepository.save(ledger);

        // 9. Close the loan if every schedule is now paid
        List<LoanPayment> allSchedules =
                loanPaymentRepository.findByLoan_LoanId(loan.getLoanId());
        boolean allPaid = allSchedules.stream()
                .allMatch(p -> "Paid".equalsIgnoreCase(p.getPaymentStatus()));
        if (allPaid) {
            loan.setLoanStatus("Completed");
        }
        loanRepository.save(loan);

        log.info("Repayment recorded: receipt={} loan={} schedule={} total={}",
                receiptNumber, loan.getLoanId(), payment.getPaymentId(), totalAmount);

        RepaymentResponse response = new RepaymentResponse(
                transaction.getTransactionId(),
                receiptNumber,
                paymentMethod,
                request.getBankReference(),
                totalAmount,
                new AllocationResponse(penaltyAllocation, serviceFeeAllocation,
                        interestAllocation, principalAllocation),
                payment.getPaymentStatus(),
                loan.getOutstandingBalance(),
                bankVerificationStatus,
                request.getCurrency()
        );
        return new RepaymentResult(response, false);
    }

    private RepaymentResponse buildReplayResponse(PaymentTransaction txn) {
        CollectionLedger ledger = collectionLedgerRepository
                .findByTransactionId(txn.getTransactionId())
                .orElse(null);

        AllocationResponse allocation = ledger == null
                ? new AllocationResponse(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO)
                : new AllocationResponse(ledger.getPenaltyAmount(), ledger.getServiceFeeAmount(),
                ledger.getInterestAmount(), ledger.getPrincipalAmount());

        LoanPayment payment = txn.getPayment();
        String scheduleStatus = payment == null ? null : payment.getPaymentStatus();
        BigDecimal remainingPrincipal = (payment != null && payment.getLoan() != null)
                ? payment.getLoan().getOutstandingBalance() : null;

        return new RepaymentResponse(
                txn.getTransactionId(),
                txn.getReceiptNumber(),
                txn.getPaymentMethod(),
                txn.getBankReference(),
                txn.getAmount(),
                allocation,
                scheduleStatus,
                remainingPrincipal,
                txn.getBankVerificationStatus(),
                txn.getCurrency()
        );
    }

    private Employee resolveEmployee(Integer receivedByEmployeeId) {
        if (receivedByEmployeeId != null) {
            return employeeRepository.findById(receivedByEmployeeId).orElse(null);
        }
        return employeeRepository.findByUserId(getCurrentUserId()).orElse(null);
    }

    private String generateReceiptNumber() {
        return "RCP-" + LocalDate.now().getYear() + "-"
                + String.format("%04d", System.currentTimeMillis() % 10000);
    }

    private BigDecimal nz(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
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
