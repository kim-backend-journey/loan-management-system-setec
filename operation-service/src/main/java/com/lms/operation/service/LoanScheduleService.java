package com.lms.operation.service;

import com.lms.operation.dto.response.LoanScheduleDetailResponse;
import com.lms.operation.dto.response.PaginatedDueInstallmentResponse;
import com.lms.operation.dto.response.PaginatedLoanScheduleResponse;

import java.time.LocalDate;

public interface LoanScheduleService {

    PaginatedLoanScheduleResponse getLoanSchedules(String status,
                                                   LocalDate fromDate,
                                                   LocalDate toDate,
                                                   Integer employeeId,
                                                   Integer branchId,
                                                   String search,
                                                   Integer page,
                                                   Integer size);

    LoanScheduleDetailResponse getLoanScheduleDetail(Integer loanId);

    /** Unpaid installments due today. Officers see their own loans; admins see all, optionally one officer's. */
    PaginatedDueInstallmentResponse getDueToday(Integer employeeId, Integer page, Integer size);

    /** Unpaid installments whose due date has passed, oldest first. Same scoping as {@link #getDueToday}. */
    PaginatedDueInstallmentResponse getOverdue(Integer employeeId, Integer page, Integer size);
}
