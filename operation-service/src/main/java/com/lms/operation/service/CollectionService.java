package com.lms.operation.service;

import com.lms.operation.dto.request.CloseDayRequest;
import com.lms.operation.dto.response.DailyClosingResponse;
import com.lms.operation.dto.response.DailyCollectionResponse;
import com.lms.operation.dto.response.DailyPaymentItemResponse;

import java.time.LocalDate;
import java.util.List;

public interface CollectionService {

    DailyCollectionResponse getDailyEmployeeCollection(Integer employeeId, LocalDate date);

    /** Every payment received on the date. Officers see their own; admins see all, or one officer's. */
    List<DailyPaymentItemResponse> getDailyPayments(LocalDate date, Integer employeeId);

    DailyClosingResponse closeDay(Integer employeeId, CloseDayRequest request);

    DailyClosingResponse getDayClosing(Integer employeeId, LocalDate date);
}
