package com.lms.auth.exception;

import java.util.List;

public record ErrorResponse(Error error) {

    public record Error(String code, List<String> reasons) {}

    // Factory — single reason
    public static ErrorResponse of(String code, String reason) {
        return new ErrorResponse(new Error(code, List.of(reason)));
    }

    // Factory — multiple reasons (validation errors)
    public static ErrorResponse of(String code, List<String> reasons) {
        return new ErrorResponse(new Error(code, reasons));
    }
}