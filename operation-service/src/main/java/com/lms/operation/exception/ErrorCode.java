package com.lms.operation.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    NOTIFICATION_NOT_FOUND("NOTIFICATION_NOT_FOUND", "Notification not found", HttpStatus.NOT_FOUND),

    RISK_ASSESSMENT_NOT_FOUND("RISK_ASSESSMENT_NOT_FOUND", "Risk assessment not found", HttpStatus.NOT_FOUND),
    LOAN_APPROVAL_NOT_FOUND("LOAN_APPROVAL_NOT_FOUND", "Loan approval not found", HttpStatus.NOT_FOUND),
    APPLICATION_NOT_APPROVED("APPLICATION_NOT_APPROVED", "Application is not approved", HttpStatus.BAD_REQUEST),
    LOAN_ALREADY_DISBURSED("LOAN_ALREADY_DISBURSED", "Loan already disbursed", HttpStatus.CONFLICT),
    //LOAN_NOT_FOUND("LOAN_NOT_FOUND", "Loan not found", HttpStatus.NOT_FOUND),
    //APPLICATION_NOT_FOUND("APPLICATION_NOT_FOUND", "Application not found", HttpStatus.NOT_FOUND),

    DOCUMENT_NOT_FOUND("DOCUMENT_NOT_FOUND", "Document not found", HttpStatus.NOT_FOUND),
    GUARANTOR_NOT_FOUND("GUARANTOR_NOT_FOUND", "Guarantor not found", HttpStatus.NOT_FOUND),
    COLLATERAL_NOT_FOUND("COLLATERAL_NOT_FOUND", "Collateral not found", HttpStatus.NOT_FOUND),


    // Customer
    CUSTOMER_NOT_FOUND("CUSTOMER_NOT_FOUND", "Customer not found", HttpStatus.NOT_FOUND),
    CUSTOMER_ALREADY_EXISTS("CUSTOMER_ALREADY_EXISTS", "Customer already exists", HttpStatus.CONFLICT),

    // Loan Product
    LOAN_PRODUCT_NOT_FOUND("LOAN_PRODUCT_NOT_FOUND", "Loan product not found", HttpStatus.NOT_FOUND),

    // Loan Application
    APPLICATION_NOT_FOUND("APPLICATION_NOT_FOUND", "Application not found", HttpStatus.NOT_FOUND),
    INVALID_APPLICATION_STATUS("INVALID_APPLICATION_STATUS", "Invalid application status transition", HttpStatus.BAD_REQUEST),
    APPLICATION_AMOUNT_EXCEEDS_LIMIT("APPLICATION_AMOUNT_EXCEEDS_LIMIT", "Requested amount exceeds product limit", HttpStatus.BAD_REQUEST),

    // Loan
    LOAN_NOT_FOUND("LOAN_NOT_FOUND", "Loan not found", HttpStatus.NOT_FOUND),

    // Payment
    PAYMENT_NOT_FOUND("PAYMENT_NOT_FOUND", "Payment not found", HttpStatus.NOT_FOUND),
    PAYMENT_ALREADY_PAID("PAYMENT_ALREADY_PAID", "Payment is already paid", HttpStatus.BAD_REQUEST),

    // General
    VALIDATION_FAILED("VALIDATION_FAILED", "Request validation failed", HttpStatus.BAD_REQUEST),
    INTERNAL_ERROR("INTERNAL_ERROR", "An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR),
    ACCESS_DENIED("ACCESS_DENIED", "You do not have permission", HttpStatus.FORBIDDEN);


    private final String code;
    private final String defaultReason;
    private final HttpStatus httpStatus;
}