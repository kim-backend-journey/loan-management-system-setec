package com.lms.operation.service;

import com.lms.operation.domain.Loan;
import com.lms.operation.domain.LoanPayment;
import com.lms.operation.domain.PaymentTransaction;
import com.lms.operation.dto.request.RecordPaymentRequest;
import com.lms.operation.dto.response.LoanPaymentResponse;
import com.lms.operation.exception.AppException;
import com.lms.operation.exception.ErrorCode;
import com.lms.operation.repository.LoanPaymentRepository;
import com.lms.operation.repository.LoanRepository;
import com.lms.operation.repository.PaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanPaymentServiceImpl implements LoanPaymentService {

    private final LoanPaymentRepository loanPaymentRepository;
    private final LoanRepository loanRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;

    @Override
    public List<LoanPaymentResponse> getPayments(Integer loanId) {
        return loanPaymentRepository.findByLoan_LoanId(loanId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public LoanPaymentResponse getPayment(Integer loanId, Integer paymentId) {
        LoanPayment payment = loanPaymentRepository.findById(paymentId)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));
        return toResponse(payment);
    }

    @Override
    @Transactional
    public LoanPaymentResponse recordPayment(Integer loanId, Integer paymentId, RecordPaymentRequest request) {
        LoanPayment payment = loanPaymentRepository.findById(paymentId)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));

        // Already paid?
        if ("Paid".equalsIgnoreCase(payment.getPaymentStatus())) {
            throw new AppException(ErrorCode.PAYMENT_ALREADY_PAID);
        }

        Loan loan = payment.getLoan();
        LocalDateTime now = LocalDateTime.now();
        BigDecimal amount = request.getAmount();

        // Update payment
        payment.setAmountPaid(amount);
        payment.setPaymentDate(now);
        payment.setPaidBy("Cashier");
        payment.setPaymentStatus(
                amount.compareTo(payment.getAmountDue()) >= 0 ? "Paid" : "Partial"
        );
        loanPaymentRepository.save(payment);

        // Update loan outstanding balance
        loan.setOutstandingBalance(loan.getOutstandingBalance().subtract(amount));

        // Create transaction record
        String referenceNumber = (request.getReferenceNumber() == null || request.getReferenceNumber().isBlank())
                ? "TXN-" + System.currentTimeMillis()
                : request.getReferenceNumber();

        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setPayment(payment);
        transaction.setPaymentMethod(request.getPaymentMethod());
        transaction.setAmount(amount);
        transaction.setReferenceNumber(referenceNumber);
        transaction.setTransactionStatus("SUCCESS");
        transaction.setReceivedBy(getCurrentUserId());
        transaction.setTransactionDate(now);
        paymentTransactionRepository.save(transaction);

        // If all payments are Paid → mark loan Completed
        boolean allPaid = loanPaymentRepository.findByLoan_LoanId(loan.getLoanId()).stream()
                .allMatch(p -> "Paid".equalsIgnoreCase(p.getPaymentStatus()));
        if (allPaid) {
            loan.setLoanStatus("Completed");
        }
        loanRepository.save(loan);

        return toResponse(payment);
    }

    private LoanPaymentResponse toResponse(LoanPayment p) {
        return new LoanPaymentResponse(
                p.getPaymentId(),
                p.getLoan().getLoanId(),
                p.getPaymentNo(),
                p.getDueDate(),
                p.getAmountDue(),
                p.getAmountPaid(),
                p.getRemainingBalance(),
                p.getPaymentDate(),
                p.getPaymentStatus(),
                p.getPaidBy(),
                p.getLateFee(),
                p.getPenaltyAmount(),
                p.getCreatedAt(),
                p.getUpdatedAt()
        );
    }

    private Integer getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof UsernamePasswordAuthenticationToken token) {
            Object details = token.getDetails();
            if (details instanceof Integer userId) {
                return userId;
            }
        }
        return 3;
    }
}