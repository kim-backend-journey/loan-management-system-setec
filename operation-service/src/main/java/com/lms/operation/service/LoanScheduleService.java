package com.lms.operation.service;

import com.lms.operation.dto.response.LoanScheduleDetailResponse;
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
}
