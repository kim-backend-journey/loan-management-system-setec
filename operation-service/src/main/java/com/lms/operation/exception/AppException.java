package com.lms.operation.exception;

import lombok.Getter;

@Getter
public class AppException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String customReason;

    public AppException(ErrorCode errorCode) {
        super(errorCode.getDefaultReason());
        this.errorCode = errorCode;
        this.customReason = errorCode.getDefaultReason();
    }

    public AppException(ErrorCode errorCode, String customReason) {
        super(customReason);
        this.errorCode = errorCode;
        this.customReason = customReason;
    }
}