package com.lms.operation.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateGuarantorRequest {

    @NotBlank(message = "firstName is required")
    private String firstName;

    @NotBlank(message = "lastName is required")
    private String lastName;

    @NotNull(message = "dateOfBirth is required")
    private LocalDate dateOfBirth;

    @NotBlank(message = "phoneNumber is required")
    private String phoneNumber;

    @Email(message = "email must be valid")
    private String email;

    @NotBlank(message = "nationalId is required")
    private String nationalId;

    private String address;

    private String occupation;

    private String employer;

    private BigDecimal monthlyIncome;

    private String relationshipToCustomer;
}