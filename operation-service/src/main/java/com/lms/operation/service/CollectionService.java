package com.lms.operation.service;

import com.lms.operation.dto.response.DailyCollectionResponse;

import java.time.LocalDate;

public interface CollectionService {

    DailyCollectionResponse getDailyEmployeeCollection(Integer employeeId, LocalDate date);
}
