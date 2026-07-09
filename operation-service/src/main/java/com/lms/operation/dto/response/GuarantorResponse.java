package com.lms.operation.dto.response;

import com.lms.operation.domain.Guarantor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record GuarantorResponse(
        Integer guarantorId,
        Integer applicationId,
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        String phoneNumber,
        String email,
        String nationalId,
        String address,
        String occupation,
        String employer,
        BigDecimal monthlyIncome,
        String relationshipToCustomer,
        String verificationStatus,
        Integer verifiedBy,
        LocalDateTime verifiedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static GuarantorResponse from(Guarantor g) {
        return new GuarantorResponse(
                g.getGuarantorId(),
                g.getApplication() != null ? g.getApplication().getApplicationId() : null,
                g.getFirstName(),
                g.getLastName(),
                g.getDateOfBirth(),
                g.getPhoneNumber(),
                g.getEmail(),
                g.getNationalId(),
                g.getAddress(),
                g.getOccupation(),
                g.getEmployer(),
                g.getMonthlyIncome(),
                g.getRelationshipToCustomer(),
                g.getVerificationStatus(),
                g.getVerifiedBy(),
                g.getVerifiedAt(),
                g.getCreatedAt(),
                g.getUpdatedAt()
        );
    }
}