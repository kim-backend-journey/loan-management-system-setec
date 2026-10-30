package com.lms.operation.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * One payment received on the day. The principal/interest/penalty split is only known for payments
 * recorded through POST /repayments (from the collection ledger); it is null for the others.
 */
public record DailyPaymentItemResponse(
        Integer transactionId,
        String receiptNumber,
        LocalDateTime paidAt,
        BigDecimal amount,
        String currency,
        String paymentMethod,
        String bankVerificationStatus,
        Integer loanId,
        String loanNumber,
        Integer scheduleId,
        Integer paymentNo,
        String installmentStatus,
        Integer customerId,
        String customerName,
        Integer receivedByUserId,
        String receivedByName,
        BigDecimal principal,
        BigDecimal interest,
        BigDecimal penalty
) {
}
