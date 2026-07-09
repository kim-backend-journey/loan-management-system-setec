package com.lms.operation.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record CustomerResponse(
        Integer customerId,
        Integer userId,
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        String gender,
        String phoneNumber,
        String email,
        String nationalId,
        String address,
        String profileImage,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}