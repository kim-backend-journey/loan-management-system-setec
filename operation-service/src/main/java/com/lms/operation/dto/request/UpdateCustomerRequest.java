package com.lms.operation.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Data;
import java.time.LocalDate;

@Data
public class UpdateCustomerRequest {

    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String gender;
    private String phoneNumber;

    @Email(message = "Invalid email format")
    private String email;

    private String address;
    private String profileImage;
}